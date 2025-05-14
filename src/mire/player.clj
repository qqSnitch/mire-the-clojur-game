(ns mire.player
(:require [clojure.java.io :as io]))

(def ^:dynamic *current-room*)
(def ^:dynamic *inventory*)
(def ^:dynamic *name*)
(def ^:dynamic *player-out*) ; Изменили имя, чтобы избежать конфликта

(def prompt "> ")
(def streams (ref {}))

(defn carrying? [thing]
(some #{(keyword thing)} @*inventory*))

(defn disconnect-player
"Disconnect player with optional death message."
[death-message]
(try
(when death-message
(binding [*out* *player-out*]
(println death-message)
(flush)))
(finally
(System/exit 0))))
