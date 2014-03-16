(ns teletype.store)

(defprotocol TStore
  (read-md [this])
  (write-md [this markdown])
  (delete-md [this]))
