package ds.gae.tasks;

import java.util.List;

import com.google.appengine.api.taskqueue.DeferredTask;

import ds.gae.CarRentalModel;
import ds.gae.EmailSender;
import ds.gae.entities.Quote;

public class ConfirmQuotesTask implements DeferredTask {
	
	private static final long serialVersionUID = 1L;
	
	private List<Quote> quotes;
	
	public ConfirmQuotesTask(List<Quote> quotes) {
		this.quotes = quotes;
	}

	@Override
	public void run() {
		System.out.println("Confirm quotes task start");
		
		EmailSender.sendMail("Your request is being handled pls wait");
		
		try {
			CarRentalModel.get().confirmQuotes(quotes);
			EmailSender.sendMail("Your request has finished");
		} catch(Exception e) {
			EmailSender.sendMail("Your request has failed, info: " + e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("Confirm quotes task finished");
	}
	
}
