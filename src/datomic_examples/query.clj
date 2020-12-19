(ns datomic-examples.query
  (:require [datomic.api :as d]
            [datomic-examples.rule :as d-e.rule]))

(def uri "datomic:dev://localhost:4334/mbrainz-1968-1973")
(def conn (d/connect uri))
(def db (d/db conn))

(defn find-artist-info-by-name
  "What is the type and gender of an artist?"
  [artist-name]
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

(defn find-tracks-by-artist
  "What are the titles of all the tracks an artist played on?"
  [artist-name]
  (d/q '[:find ?title
         :in $ % ?artist-name
         :where
         (artist-track ?artist-name ?track)
         [?track :track/name ?title]]
       db
       [d-e.rule/artist-track]
       artist-name))

(defn find-track-info-by-artist
  "What are the titles, album names, and release years of an artist tracks?"
  [artist-name]
  (d/q '[:find ?title ?album ?year
         :in $ % ?artist-name
         :where
         (artist-track ?artist-name ?track)
         (track-info ?track ?title _ ?album ?year)]
       db
       [d-e.rule/artist-track d-e.rule/track-info]
       artist-name))

(defn find-track-info-by-artist-before-year
  "What are the titles, album names, and release years of an artist tracks?"
  [artist-name year-limit]
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

(defn find-track-info-with-string
    "What are the titles, artists, album names, and release years of all tracks
     having the given word in their titles?"
    [search]
    (d/q '[:find ?title ?artist ?album ?year
           :in $ % ?search
           :where
           (track-search ?search ?track)
           (track-info ?track ?title ?artist ?album ?year)]
         db
         [d-e.rule/track-search d-e.rule/track-info]
         search))

(defn find-release-info-by-artist
  [artist-name]
  (d/q '[:find ?album ?year (distinct ?artist-names)
         :in $ % ?artist-name
         :where
         (artist-release ?artist-name ?release)
         (release-info ?release ?artist-names ?album ?year)]
       db
       [d-e.rule/artist-release d-e.rule/release-info]
       artist-name))
