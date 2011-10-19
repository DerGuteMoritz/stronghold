(ns stronghold.core)

(defn allowed? [policy access]
  (= :allow
     (some (fn [perm]
             (case perm
               (:allow :deny) perm
               :pass false
               (Exception. "rules must return :allow, :deny or :pass")))
           (map (fn [rule]
                  (rule access))
                policy))))

(defn deny [deny?]
  (fn [access]
    (if (deny? access)
      :deny
      :pass)))

(defn allow [allow?]
  (fn [access]
    (if (allow? access)
      :allow
      :pass)))