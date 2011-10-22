(ns stronghold.core)

;; Implementation rationale: I use a recursion here instead of a lazy
;; sequence because sequence chunking would evaluate more rules than
;; possibly required. Also the code is more compact this way.
(defn allowed? [policy access]
  (and (seq policy)
       (let [[[match? allow?] & more] policy]
         (if (match? access)
           (if (fn? allow?)
             (allow? access)
             allow?)
           (recur more access)))))

(defn allow
  ([pred] [pred true])
  ([] [(constantly true) true]))

(defn deny
  ([pred] [pred false])
  ([] [(constantly true) false]))

