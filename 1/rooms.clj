(ns mire.rooms
    (:require [clojure.java.io :as io]))

(def rooms (ref {}))
(def player-inventories (atom {}))

(defn init-player-inventory
  "Initialize inventory for a new player"
  [player-name]
  (swap! player-inventories assoc player-name (ref #{}))
  (get @player-inventories player-name))

(defn get-player-inventory
  "Get player's inventory ref"
  [player-name]
  (get @player-inventories player-name))

(defn player-has-item?
  "Check if player has specific item"
  [player-name item]
  (let [inv (get-player-inventory player-name)]
    (when inv
      (@inv (keyword item)))))


(defn load-room [rooms file]
  (let [room (read-string (slurp (.getAbsolutePath file)))]
    (conj rooms
          {(keyword (.getName file))
           {:name (keyword (.getName file))
            :desc (:desc room)
            :exits (ref (:exits room))
            :items (ref (or (:items room) #{}))
            :inhabitants (ref #{})}})))

(defn load-rooms
  "Given a dir, return a map with an entry corresponding to each file
  in it. Files should be maps containing room data."
  [rooms dir]
  (dosync
   (reduce load-room rooms
           (.listFiles (java.io.File. dir)))))

(defn add-rooms
  "Look through all the files in a dir for files describing rooms and add
  them to the mire.rooms/rooms map."
  [dir]
  (dosync
   (alter rooms load-rooms dir)))

(defn room-contains?
  [room thing]
  (@(:items room) (keyword thing)))


