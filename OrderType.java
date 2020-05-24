package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;
import au.edu.sydney.cpa.erp.ordering.ScheduledOrder;

import java.time.LocalDateTime;
import java.util.*;

/**
 * This class is an abstraction of the kinds of orders that can be created- Critical or Normal
 * The common methods have been implemented but the methods with different implementations for Critical and Normal
 * will be implemented in their respective classes.
 * This refactoring follows the Bridge pattern
 */

public abstract class OrderType implements ScheduledOrder {

    protected Map<Report, Integer> reports = new HashMap<>();
    protected boolean finalised=false;
    protected final int id;
    protected LocalDateTime date;
    protected int client;
    protected double criticalLoading;
    protected int maxCountedEmployees;

    public OrderType(int id, int client, LocalDateTime date, double criticalLoading, int maxCountedEmployees) {
        this.id = id;
        this.client = client;
        this.date = date;
        this.criticalLoading = criticalLoading;
        this.maxCountedEmployees=maxCountedEmployees;
    }

    @Override
    public LocalDateTime getOrderDate() {
        return date;
    }

    @Override
    public void setReport(Report report, int employeeCount) {

        if (finalised) throw new IllegalStateException("Order was already finalised.");

        // We can't rely on equal reports having the same object identity since they get
        // rebuilt over the network, so we have to check for presence and same values

        for (Report contained: reports.keySet()) {
            if (contained.getCommission() == report.getCommission() &&
                    contained.getReportName().equals(report.getReportName()) &&
                    Arrays.equals(contained.getLegalData(), report.getLegalData()) &&
                    Arrays.equals(contained.getCashFlowData(), report.getCashFlowData()) &&
                    Arrays.equals(contained.getMergesData(), report.getMergesData()) &&
                    Arrays.equals(contained.getTallyingData(), report.getTallyingData()) &&
                    Arrays.equals(contained.getDeductionsData(), report.getDeductionsData())) {
                report = contained;
                break;
            }
        }

        reports.put(report, employeeCount);
    }

    @Override
    public Set<Report> getAllReports() {
        return reports.keySet();
    }

    //this can be modified with value object
    @Override
    public int getReportEmployeeCount(Report report) {
        // We can't rely on equal reports having the same object identity since they get
        // rebuilt over the network, so we have to check for presence and same values

        for (Report contained: reports.keySet()) {
            if (contained.getCommission() == report.getCommission() &&
                    contained.getReportName().equals(report.getReportName()) &&
                    Arrays.equals(contained.getLegalData(), report.getLegalData()) &&
                    Arrays.equals(contained.getCashFlowData(), report.getCashFlowData()) &&
                    Arrays.equals(contained.getMergesData(), report.getMergesData()) &&
                    Arrays.equals(contained.getTallyingData(), report.getTallyingData()) &&
                    Arrays.equals(contained.getDeductionsData(), report.getDeductionsData())) {
                report = contained;
                break;
            }
        }
        Integer result = reports.get(report);
        return null == result ? 0 : result;
    }

    @Override
    public int getClient() {
        return client;
    }

    @Override
    public void finalise() {
        this.finalised = true;
    }

    protected Map<Report, Integer> getReports() {
        return reports;
    }

    @Override
    public int getOrderID() {
        return id;
    }

    protected boolean isFinalised() {
        return finalised;
    }

    public double getCriticalLoading(){
        return criticalLoading;
    }

    public int getMaxCountedEmployees(){
        return maxCountedEmployees;
    }

    @Override
    public String shortDesc() {
        return String.format("ID:%s $%,.2f", id, getTotalCommission());
    }


}
