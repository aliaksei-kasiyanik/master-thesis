package com.akasiyanik.trip.cplex.solution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author akasiyanik
 *         6/6/17
 */
public class RouteSolution {

    private List<ProblemSolution> solutions = new ArrayList<>();

    private long arcsCount;

    public long getTotalTime() {
        return solutions.stream().mapToLong(ProblemSolution::getTime).sum();
    }

    public List<ProblemSolution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<ProblemSolution> solutions) {
        this.solutions = solutions;
    }

    public long getArcsCount() {
        return arcsCount;
    }

    public void setArcsCount(long arcsCount) {
        this.arcsCount = arcsCount;
    }

}
