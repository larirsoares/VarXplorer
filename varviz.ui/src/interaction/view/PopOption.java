package interaction.view;

public class PopOption {

	private String info;
	private Boolean state = false;
	private String from;
	private String to;
	
	public PopOption(String info, String from, String to) {
		this.info = info;
		this.from = from;
		this.to = to;
		this.state = false;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String op) {
		this.info = op;
	}
	public Boolean getState() {
		return state;
	}
	public void setState(Boolean state) {
		this.state = state;
	}
	public void markState(){
		if(this.state){
			this.state = false;
		}else{
			this.state = true;
		}
	}
	@Override
	public String toString() {
		return from + " to " + to + " + info " + info + " state: " + state;		
	}
	
}
