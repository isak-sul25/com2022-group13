package main;

public class Menu {
	private String theme;
	private String starter;
	private String mainDish;
	private String dessert;
	private String drink;
	
	public Menu(String theme, String starter, String mainDish, String dessert, String drink) {
		super();
		this.theme = theme;
		this.starter = starter;
		this.mainDish = mainDish;
		this.dessert = dessert;
		this.drink = drink;
	}

	@Override
	public String toString() {
		return "Menu [theme=" + theme + ", "
				+ "starter=" + starter + ", "
				+ "mainDish=" + mainDish + ", "
				+ "dessert=" + dessert+ ", "
				+ "drink=" + drink + "]";
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

}
