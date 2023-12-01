package edu.coursera.distributed;

import org.apache.spark.api.java.JavaRDD;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

/**
 * A wrapper class for the implementation of a single iteration of the iterative
 * PageRank algorithm.
 */
public final class PageRank {
    /**
     * Default constructor.
     */
    private PageRank() {
    }

    /**
     *
     * @param sites The connectivity of the website graph, keyed on unique
     *              website IDs.
     * @param ranks The current ranks of each website, keyed on unique website
     *              IDs.
     * @return The new ranks of the websites graph, using the PageRank
     *         algorithm to update site ranks.
     */
    public static JavaPairRDD<Integer, Double> sparkPageRank(
            final JavaPairRDD<Integer, Website> sites,
            final JavaPairRDD<Integer, Double> ranks) {
        JavaPairRDD<Integer, Double> newRanks = sites
                        .join(ranks)
                        .flatMapToPair(kv -> flatMap(kv));

        return newRanks.reduceByKey((r1, r2) -> r1 + r2).mapValues(v -> 0.15 + 0.85 * v);
    }

    private static Iterable<Tuple2<Integer, Double>> flatMap(Tuple2<Integer, Tuple2<Website, Double>> kv) {
        Website edges = kv._2()._1();
        Double currentRank = kv._2()._2();

        List<Tuple2<Integer, Double>> contribs = new LinkedList<>();
        Iterator<Integer> edgeIter = edges.edgeIterator();
        while (edgeIter.hasNext()) {
            int target = edgeIter.next();
            contribs.add(new Tuple2<>(target, currentRank / (double) edges.getNEdges()));
        }
        return contribs;
    }
}
