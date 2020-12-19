(ns datomic-examples.rule)

(def artist-track
  ;; Given ?artist-name bound to artist name, binds ?track
  ;; to the corresponding set of track entity-ids
  '[(artist-track ?artist-name ?track)
    [?artist :artist/name ?artist-name]
    [?track :track/artists ?artist]])

(def artist-release
  '[(artist-release ?artist-name ?release)
    [?artist :artist/name ?artist-name]
    [?release :release/artists ?artist]])

(def track-info
  '[(track-info ?track ?title ?artist-name ?album ?year)
    [?track :track/name ?title]
    [?media :medium/tracks ?track]
    [?release :release/media ?media]
    [?release :release/artists ?artist]
    [?artist :artist/name ?artist-name]
    [?release :release/name ?album]
    [?release :release/year ?year]])

(def track-search
  ;; Given ?search bound to a string, binds ?track
  ;; to the corresponding set of tracks entity-ids
  ;; that the track name contains the search string
  '[(track-search ?search ?track)
    [?track :track/name ?name]
    [(fulltext $ :track/name ?search) [[?track ?name]]]])

(def release-info
  '[(release-info ?release ?artist-names ?album ?year)
    [?release :release/artists ?artist]
    [?artist :artist/name ?artist-names]
    [?release :release/name ?album]
    [?release :release/year ?year]])
