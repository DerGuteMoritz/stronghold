(ns stronghold.test
  (:use [stronghold.core])
  (:use [lazytest.describe :only [describe it do-it testing given]]
        [lazytest.expect :only [expect]]
        [lazytest.expect.thrown :only [throws?]]))

(describe "allowed?"
  (it "should deny access by default"
    (not (allowed? [] {})))

  (do-it "should accept a vector of vectors as a policy containing a matcher predicate and a decision whether to deny or allow something"
    (expect (allowed? [[(constantly true) true]] {}))
    (expect (not (allowed? [[(constantly true) false]] {}))))

  (it "should check the next rule if a rule doesn't match"
    (allowed? [[(constantly false) false]
               [(constantly false) false]
               [(constantly true)  true]]
              {}))

  (testing "passes its argument to the decision procedure, too"
    (given [policy [[:banned (fn [x] (not (:banned x)))] ; just to check that the argument is actually passed
                    [:admin  (constantly true)]]]
      (it "should deny ordinary people"
        (not (allowed? policy {:name "foo"})))
      (it "should allow admins"
        (allowed? policy {:name "bar" :admin true}))
      (it "should deny banned admins"
        (not (allowed? policy {:name "bar" :admin true :banned true}))))))

(describe "allow / deny"
  (given [policy [(allow even?)
                  (allow (partial = 3))
                  (deny odd?)
                  (allow (constantly true))]] ; just to make sure if the previous rule works
    (do-it "should allow even numbers"
      (expect (allowed? policy 2))
      (expect (allowed? policy 0)))
    (it "should allow 3 as an exception"
      (allowed? policy 3))
    (it "should deny other odd numbers"
      (not (allowed? policy 9)))))