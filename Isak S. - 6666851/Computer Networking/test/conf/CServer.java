package conf;

import java.util.ArrayList;
import java.util.Arrays;

import main.Menu;
import main.Server;

public class CServer {
	public static void main(String[] args) {
		Menu Monday = new Menu("Monday", "Chinese", "Sliced Tofu Salad", "Kung Pao Chicken", "Fried Bananas",
				"Bubble Tea");
		Menu Tuesday = new Menu("Tuesday", "Indian", "Vegetable Samosas", "Butter Chicken", "Modak", "Masala Chai");
		Menu Wednesday = new Menu("Wednesday", "Middle Eastern", "Fattoush Salad", "Pomegranate Chicken", "Halva",
				"Limonana");
		Menu Thursday = new Menu("Thursday", "Mediterranean", "Baba Ganoush", "Spanakopita", "Baklava", "Iced Tea");
		Menu Friday = new Menu("Friday", "American", "Mini Burgers", "Buffalo Chicken", "Apple Pie", "Soda");
		Menu[] menusAr = { Monday, Tuesday, Wednesday, Thursday, Friday };
		
		ArrayList<Menu> menuList = new ArrayList<>(Arrays.asList(menusAr));
		
		Server s = new Server(menuList);
		//1
		System.out.println("Server port: " + Server.port + "\n");
	}

}
