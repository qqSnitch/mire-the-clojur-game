(ns mire.player
    (:require [mire.rooms :as rooms]))

(def ^:dynamic *current-room*)
(def ^:dynamic *inventory*)
(def ^:dynamic *name*)

(def prompt "> ")
(def streams (ref {}))

(defn carrying? [thing]
  (some #{(keyword thing)} @*inventory*))

(defn init-player [name]
  (dosync
   (let [inventory (ref #{})]
     (rooms/init-player-inventory name)
     (ref-set *inventory* (rooms/get-player-inventory name))
    *inventory*)))
