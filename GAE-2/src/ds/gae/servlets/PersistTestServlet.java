package ds.gae.servlets;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ds.gae.CarRentalModel;
import ds.gae.ReservationException;
import ds.gae.entities.Quote;
import ds.gae.entities.ReservationConstraints;
import ds.gae.view.JSPSite;
import ds.gae.view.ViewTools;

public class PersistTestServlet extends HttpServlet {
	private static final long serialVersionUID = -4694162076388862047L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String companyName = "Hertz";
		String userName = "Pieter";
		
		req.getSession().setAttribute("renter", userName);
		
		try {
			boolean fullApplicationDeployed = new File(getServletContext().getRealPath(JSPSite.CREATE_QUOTES.url())).exists();

			 System.out.println("Onbetrouwbare if");
			 System.out.println(CarRentalModel.get().getReservations(userName).size() == 0);
			 System.out.println(!fullApplicationDeployed);
			if (CarRentalModel.get().getReservations(userName).size() == 0 && fullApplicationDeployed) {

				List<Quote> quotes = new ArrayList<Quote>();
				
				ReservationConstraints c = new ReservationConstraints(
						ViewTools.DATE_FORMAT.parse("01.02.2011"), 
						ViewTools.DATE_FORMAT.parse("01.03.2011"), "Compact");
			
				quotes.add(CarRentalModel.get().createQuote(companyName, userName, c));

				ReservationConstraints c2 = new ReservationConstraints(
						ViewTools.DATE_FORMAT.parse("01.02.2011"), 
						ViewTools.DATE_FORMAT.parse("01.03.2011"), "Eco");
			
				quotes.add(CarRentalModel.get().createQuote("Dockx", userName, c2));

//				for (int i = 0; i < 100; i++) {
//					ReservationConstraints c3 = new ReservationConstraints(
//							ViewTools.DATE_FORMAT.parse("01.02.2011"), 
//							ViewTools.DATE_FORMAT.parse("01.03.2011"), "Eco");
//				
//					quotes.add(CarRentalModel.get().createQuote("Dockx", userName, c3));
//					
//				}
				
				System.out.println("Confirm quotes");
				CarRentalModel.get().confirmQuotes(quotes);
			}
			
			resp.sendRedirect(JSPSite.PERSIST_TEST.url());
		} catch (ParseException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ViewTools.stacktraceToHTMLString(e));
		} catch (ReservationException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ViewTools.stacktraceToHTMLString(e));			
		}
	}
}
