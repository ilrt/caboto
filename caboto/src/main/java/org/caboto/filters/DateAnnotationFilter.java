/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.caboto.filters;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.*;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueDateTime;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.TripleCollector;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ecjet
 */
class DateAnnotationFilter extends AnnotationFilterBase {

    private static final String DATETIME_FORMAT = "dd-MM-yyyy";
    private Date date;
    private boolean isFrom;

    public DateAnnotationFilter(String valueS, boolean isFrom) {
        this.isFrom = isFrom;
        // Define date format
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT);
        try {
            // Create a date object
            date = format.parse(valueS);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Illegal date: " + valueS);
        }
    }

    @Override
    public void augmentBlock(TripleCollector arg0, String annotationBodyVar, String annotationHeadVar) {
    }

    @Override
    public void visit(ElementGroup arg0) {
        super.visit(arg0);
        if (inDefaultGraph()) {
            Var varCreated = Var.alloc("created");
            // Create dateTime expression
            Expr exprDate = new NodeValueDateTime(dateToXSDDateTime(date));
            // Adds a filter.  Need to wrap variable in a NodeVar.
            Expr exprCreated = new ExprVar(varCreated);
            Expr expr;
            if(isFrom) {
                expr = new E_GreaterThanOrEqual(exprCreated, exprDate);
            } else {
                expr = new E_LessThan(exprCreated, exprDate);
            }
            ElementFilter filter = new ElementFilter(expr);
            arg0.addElementFilter(filter);
        }
    }
    
    private XSDDateTime dateToXSDDateTime(Date date) {
        // Get a calendar
        Calendar cal = Calendar.getInstance();
        // Set time of the calendar to specified date
        cal.setTime(date);
        // Increment if 'to'
        // <= 2011-11-11T00:00:00Z becomes < 2011-11-12T00:00:00Z
        if(!isFrom)
            cal.add(Calendar.DAY_OF_MONTH, +1);
        // return the newly created object using calendar
        return new XSDDateTime(cal);
    }
}
