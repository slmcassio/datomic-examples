(ns datomic-examples.query
  (:require [datomic.api :as d]
            [datomic-examples.rule :as d-e.rule]))

(defn new-artist
  [name type gender start-year conn]
  (let [temp-id (d/tempid :db.part/user)
        gid     (java.util.UUID/randomUUID)]
    (d/transact conn [[:db/add temp-id :artist/gid gid]
                      [:db/add temp-id :artist/name name]
                      [:db/add temp-id :artist/sortName name]
                      [:db/add temp-id :artist/type type]
                      [:db/add temp-id :artist/gender gender]
                      [:db/add temp-id :artist/startYear start-year]])))

(defn artist-id-by-name
  [name db]
  (-> (d/q '[:find ?artist
             :in $ ?artist-name
             :where [?artist :artist/name ?artist-name]]
           db
           name)
      first
      first))

(defn retract-artist-by-name
  [name conn]
  (let [artist-id (artist-id-by-name name (d/db conn))]
    (d/transact conn [[:db/retractEntity artist-id]])))

(defn all-artists
  [db]
  (d/q '[:find (pull ?artist [* {:artist/type [*]}])
         :in $
         :where
         [?artist :artist/name ?artist-name]]
       db))

(defn artist-info-by-name
  "What is the type and gender of an artist?"
  [artist-name db]
  (d/q '[:find ?id ?type ?gender
         :in $ ?artist-name
         :where
         [?artist :artist/name ?artist-name]
         [?artist :artist/gid ?id]
         [?artist :artist/type ?type-id]
         [?type-id :db/ident ?type]
         [?artist :artist/gender ?gender-id]
         [?gender-id :db/ident ?gender]]
       db
       artist-name))

(defn tracks-by-artist
  "What are the titles of all the tracks an artist played on?"
  [artist-name db]
  (d/q '[:find ?title
         :in $ % ?artist-name
         :where
         (artist-track ?artist-name ?track)
         [?track :track/name ?title]]
       db
       [d-e.rule/artist-track]
       artist-name))

(defn track-info-by-artist
  "What are the titles, album names, and release years of an artist tracks?"
  [artist-name db]
  (d/q '[:find ?title ?album ?year
         :in $ % ?artist-name
         :where
         (artist-track ?artist-name ?track)
         (track-info ?track ?title _ ?album ?year)]
       db
       [d-e.rule/artist-track d-e.rule/track-info]
       artist-name))

(defn track-info-by-artist-before-year
  "What are the titles, album names, and release years of an artist tracks?"
  [artist-name year-limit db]
  (d/q '[:find ?title ?album ?year
         :in $ % ?artist-name ?year-limit
         :where
         (artist-track ?artist-name ?track)
         (track-info ?track ?title _ ?album ?year)
         [(< ?year ?year-limit)]]
       db
       [d-e.rule/artist-track d-e.rule/track-info]
       artist-name
       year-limit))

(defn track-info-with-string
  "What are the titles, artists, album names, and release years of all tracks
   having the given word in their titles?"
  [search db]
  (d/q '[:find ?title ?artist ?album ?year
         :in $ % ?search
         :where
         (track-search ?search ?track)
         (track-info ?track ?title ?artist ?album ?year)]
       db
       [d-e.rule/track-search d-e.rule/track-info]
       search))

(defn release-info-by-artist
  [artist-name db]
  (d/q '[:find ?album ?year (distinct ?artist-names)
         :in $ % ?artist-name
         :where
         (artist-release ?artist-name ?release)
         (release-info ?release ?artist-names ?album ?year)]
       db
       [d-e.rule/artist-release d-e.rule/release-info]
       artist-name))
