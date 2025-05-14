(ns mire.player)

(def ^:dynamic *current-room*)
(def ^:dynamic *inventory*)
(def ^:dynamic *name*)
(def ^:dynamic *out*) ; Добавляем динамическую переменную для вывода

(def prompt "> ")
(def streams (ref {}))

(defn carrying? [thing]
  (some #{(keyword thing)} @*inventory*))

(defn disconnect-player
  "Disconnect player with optional death message."
  [death-message]
  (try
    (when death-message
      (println death-message)
      (flush))
    (finally
      (System/exit 0)))




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
         (player/disconnect-player "You drank the poison and died!"))
       (str "You drank the " item ". It's safe."))
     (str "You're not carrying a " item "."))))





  (defn- cleanup []
  "Drop all inventory and remove player from room and player list."
  (dosync
   (doseq [item @player/*inventory*]
     (commands/discard (name item)))
   (commute player/streams dissoc player/*name*)
   (commute (:inhabitants @player/*current-room*)
            disj player/*name*)))
