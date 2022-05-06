package main;

public class Menu {
	private String theme;
	private String starter;
	private String mainDish;
	private String dessert;
	private String drink;
	private String day;
	
	public Menu(String day, String theme, String starter, String mainDish, String dessert, String drink) {
		super();
		this.day = day;
		this.theme = theme;
		this.starter = starter;
		this.mainDish = mainDish;
		this.dessert = dessert;
		this.drink = drink;
	}
	
	public String getMenu() {
		String output = "***" + this.getName() + " Menu***\n" +
						"Starter: " + this.starter + "\n" +
						"Main Dish: " + this.mainDish + "\n" +
						"Dessert: " + this.dessert + "\n" +
						"Drink: " + this.drink;
		
		return output;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getStarter() {
		return starter;
	}

	public void setStarter(String starter) {
		this.starter = starter;
	}

	public String getMainDish() {
		return mainDish;
	}

	public void setMainDish(String mainDish) {
		this.mainDish = mainDish;
	}

	public String getDessert() {
		return dessert;
	}

	public void setDessert(String dessert) {
		this.dessert = dessert;
	}

	public String getDrink() {
		return drink;
	}

	public void setDrink(String drink) {
		this.drink = drink;
	}
	
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public String getName() {
		return this.theme + " (" + this.day + ")";
	}

}
