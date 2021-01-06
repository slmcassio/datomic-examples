(ns datomic-examples.query-test
  (:require [clojure.test :refer :all]
            [datomic.api :as d]
            [datomic-examples.query :as d-e.query]))

(def uri "datomic:dev://localhost:4334/mbrainz-1968-1973")
(def conn (d/connect uri))
(def db (d/db conn))

(def artist-name "Rich Rickey")
(def artist-type :artist.type/person)
(def artist-gender :artist.gender/male)
(def artist-start-year 2007)

(deftest new-artist-test
  (testing "An artist can be added"
    (do
      (d-e.query/new-artist artist-name artist-type artist-gender artist-start-year conn)

      (is (= #{}
             (d-e.query/artist-info-by-name artist-name db)))

      (is (some? (d-e.query/artist-id-by-name artist-name (d/db conn)))))))

(deftest retract-artist-test
  (testing "An artist can be retracted"
    (do
      (is (some? (d-e.query/artist-id-by-name artist-name (d/db conn))))

      (d-e.query/retract-artist-by-name artist-name conn)

      (is (= #{}
             (d-e.query/artist-info-by-name artist-name (d/db conn)))))))

(deftest all-artists-test
  (testing "All artists can be found"
    (is (not-empty (d-e.query/all-artists db)))))

(deftest artist-info-by-name-test
  (testing "An artist can be found by name"
    (is (= #{[#uuid"76c9a186-75bd-436a-85c0-823e3efddb7f"
              :artist.type/person
              :artist.gender/female]}
           (d-e.query/artist-info-by-name "Janis Joplin" db)))))

(deftest tracks-by-artist-test
  (testing "Track titles can be found by the artist name"
    (is (= ["Wicked World"]
           (-> (d-e.query/tracks-by-artist "Black Sabbath" db)
               sort
               last)))

    (is (= 50
           (count (d-e.query/tracks-by-artist "Black Sabbath" db))))))

(deftest track-info-by-artist-test
  (testing "Track title, album and release years can be found by the artist name"
    (is (= ["Wrote a Song for Everyone" "Green River" 1969]
           (-> (d-e.query/track-info-by-artist "Creedence Clearwater Revival" db)
               sort
               last)))

    (is (= 102
           (count (d-e.query/track-info-by-artist "Creedence Clearwater Revival" db))))))

(deftest track-info-by-artist-before-year-test
  (testing "Track title, album and release years can be found by the artist name before a given year"
    (is (= #{["Gloomy" "Creedence Clearwater Revival" 1968]
             ["The Working Man" "Creedence Clearwater Revival" 1968]
             ["Porterville" "Creedence Clearwater Revival" 1968]
             ["I Put a Spell on You" "Creedence Clearwater Revival" 1968]
             ["Ninety‐Nine and a Half (Won’t Do)" "Creedence Clearwater Revival" 1968]
             ["Susie Q, Part Two" "Susie Q" 1968]
             ["Susie Q, Part One" "Susie Q" 1968]
             ["Get Down Woman" "Creedence Clearwater Revival" 1968]
             ["Suzie Q" "Creedence Clearwater Revival" 1968]
             ["Walk on the Water" "Creedence Clearwater Revival" 1968]}
           (d-e.query/track-info-by-artist-before-year "Creedence Clearwater Revival" 1969 db)))))

(deftest track-info-with-string-test
  (testing "Track title, artist, album and release years can be found a title word"
    (is (= ["You're Always Welcome to Our House" "The Clancy Brothers" "Welcome to Our House" 1970]
           (-> (d-e.query/track-info-with-string "always" db)
               sort
               last)))

    (is (= 92
           (count (d-e.query/track-info-with-string "always" db))))))

(deftest release-info-by-artist-test
  (testing "Track title, album and release years can be found by the artist name"
    (is (= [["Another Day / Oh Woman Oh Why" 1971 #{"Paul McCartney"}]
            ["McCartney" 1970 #{"Paul McCartney"}]
            ["Ram" 1971 #{"Paul McCartney" "Linda McCartney"}]]
           (d-e.query/release-info-by-artist "Paul McCartney" db)))))
