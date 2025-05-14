(defn drink
  "Drink something. If it's poison - you die."
  [item]
  (dosync
   (if (player/carrying? item)
     (if (= item "poison")
       (do
         (move-between-refs (keyword item)
                           player/*inventory*
                           (:items @player/*current-room*))
         (player/disconnect-player true)
         "You drank the poison and died. Connection will be closed.")
       (str "You drank the " item ". It's safe."))
     (str "You're not carrying a " item "."))))


(defn disconnect-player
  "Disconnect player, optionally with death message."
  [died?]
  (dosync
   (let [streams @*streams*]
     (when streams
       (when died?
         (.println (:out streams) "You died!")
         (.flush (:out streams)))
       (.close (:out streams))
       (.close (:in streams))
       (alter player/*online-players* disj *name*)
       (move-between-refs *name*
                         (:inhabitants @*current-room*)
                         player/*disconnected-players*)))))
