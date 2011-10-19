(ns stronghold.test
  (:use [stronghold.core])
  (:use [lazytest.describe :only [describe it do-it testing given]]
        [lazytest.expect :only [expect]]
        [lazytest.expect.thrown :only [throws?]]))

(describe "allowed?"
  (it "should deny access by default"
    (not (allowed? [] {})))

  (it "should allow access when a rule returns :allow"
    (allowed? [(constantly :allow)] {}))

  (it "should deny access when a rule returns :deny"
    (not (allowed? [(constantly :deny)] {})))

  (it "should check the next rule if a rule returns :pass"
    (allowed? [(constantly :pass)
               (constantly :pass)
               (constantly :allow)]
              {}))

  (it "should throw an exception when rules violate the protocol"
    (throws? Exception
             (fn []
               (allowed? [(constantly true)]))))

  (testing "passes its argument to the rules"
    (given [policy [(fn [x] (if (:banned x) :deny :pass))
                    (fn [x] (if (:admin x) :allow :pass))]]
      (it "should deny ordinary people"
        (not (allowed? policy {:name "foo"})))
      (it "should allow admins"
        (allowed? policy {:name "bar" :admin true}))
      (it "should deny banned admins"
        (not (allowed? policy {:name "bar" :admin true :banned true}))))))

(describe "allow, deny"
  (given [policy [(allow even?)
                  (allow (partial = 3))
                  (deny  odd?)
                  (constantly :allow)]] ; just to make sure if the previous rule works
    (do-it "should allow even numbers"
      (expect (allowed? policy 2))
      (expect (allowed? policy 0)))
    (it "should allow 3 as an exception"
      (allowed? policy 3))
    (it "should deny other odd numbers"
      (not (allowed? policy 9)))))