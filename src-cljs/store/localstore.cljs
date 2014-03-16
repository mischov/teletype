(ns teletype.store.localstore
  (:require [teletype.store :refer [TStore]]))

(deftype LocalTStore [store key]
  TStore
  (read-md [this]
    (when-let [md (.getItem store key)]
      md))
  (write-md [this md]
    (.setItem store key md))
  (delete-md [this]
    (.removeItem store key)))

(defn create-store
  [key]
  (LocalTStore. js/localStorage key))

