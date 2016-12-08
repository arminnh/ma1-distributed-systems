package ds.gae.tasks;

import java.util.List;

import com.google.appengine.api.taskqueue.DeferredTask;

import ds.gae.CarRentalModel;
import ds.gae.ReservationException;
import ds.gae.entities.Quote;
import ds.gae.view.JSPSite;
import ds.gae.view.ViewTools;

public class ConfirmQuotesTask implements DeferredTask {
	
	private static final long serialVersionUID = 1L;
	
	private List<Quote> quotes;
	
	public ConfirmQuotesTask(List<Quote> quotes) {
		this.quotes = quotes;
	}

	@Override
	public void run() {
		try {
			CarRentalModel.get().confirmQuotes(quotes);
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("TASK");
		/*
		 * 
		} catch (ReservationException e) {
			session.setAttribute("errorMsg", ViewTools.encodeHTML(e.getMessage()));
			resp.sendRedirect(JSPSite.RESERVATION_ERROR.url());
		 */
	}

	
}
