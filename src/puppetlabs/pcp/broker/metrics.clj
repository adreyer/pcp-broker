(ns puppetlabs.pcp.broker.metrics
  (:require [clojure.java.jmx :as jmx]
            [metrics.counters :as counters]
            [metrics.meters :as meters]
            [metrics.timers :as timers]))

(defn get-pcp-metrics
  "Returns pcp specific metrics as a map"
  [registry]
  (reduce into {}
          [(map (fn [[k v]] {k (meters/rates v)}) (.getMeters registry))
           (map (fn [[k v]] {k (counters/value v)}) (.getCounters registry))
           (map (fn [[k v]] {k {:rates (timers/rates v)
                                :mean (timers/mean v)
                                :std-dev (timers/std-dev v)
                                :percentiles (timers/percentiles v)
                                :largest (timers/largest v)
                                :smallest (timers/smallest v)}}) (.getTimers registry))]))

(defn get-memory-metrics
  "Returns memory related metrics as a map"
  []
  (dissoc (jmx/mbean "java.lang:type=Memory") :ObjectName))

(defn get-thread-metrics
  "Returns thread related metrics as a map"
  []
  (apply dissoc (jmx/mbean "java.lang:type=Threading") [:ObjectName :AllThreadIds]))
