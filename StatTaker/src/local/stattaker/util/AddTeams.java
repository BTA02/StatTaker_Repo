
package local.stattaker.util;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.PlayerDb;
import android.app.Activity;
import android.os.Bundle;

public class AddTeams extends Activity
{
	DatabaseHelper db;
	
	int a;
	
	public AddTeams(DatabaseHelper db)
	{
		a = 1;
		this.db = db;
	}
	public void addMichigan()
	{
		String t = "University Of Michigan";
		
		PlayerDb p = new PlayerDb(t, -1, "19", "Evan", "Batzer", 1, 1);
		PlayerDb p1 = new PlayerDb(t, -1, "93", "Matt", "Oates", 2, 1);
		PlayerDb p2 = new PlayerDb(t, -1, "2", "Andrew", "Axtell", 3, 1);
		PlayerDb p3 = new PlayerDb(t, -1, "404", "Malek", "Atassi", 4, 1);
		PlayerDb p4 = new PlayerDb(t, -1, "34", "Andy", "Barton", 5, 1);
		PlayerDb p5 = new PlayerDb(t, -1, "12", "Phuc", "Do", 6, 1);
		PlayerDb p6 = new PlayerDb(t, -1, "47", "Andrew", "Kim", 0, 0);
		PlayerDb p7 = new PlayerDb(t, -1, "22", "Matt", "Oppenlander", 7, 1);
		PlayerDb p8 = new PlayerDb(t, -1, "50", "Evan", "Breen", 0, 1);
		PlayerDb p9 = new PlayerDb(t, -1, "10", "Michelle", "Busch", 0, 1);
		PlayerDb p10 = new PlayerDb(t, -1, "142", "Margaret", "Burns", 0, 1);
		PlayerDb p11 = new PlayerDb(t, -1, "8", "Danielle", "DuBois", 0, 1);
		PlayerDb p12 = new PlayerDb(t, -1, "5", "Maddy", "Novack", 0, 1);
		PlayerDb p13 = new PlayerDb(t, -1, "18", "Dylan", "Schepers", 0, 1);
		PlayerDb p14 = new PlayerDb(t, -1, "25", "Zach", "Schepers", 0, 1);
		PlayerDb p15 = new PlayerDb(t, -1, "pi", "Lucas", "Mitchell", 0, 1);
		PlayerDb p16 = new PlayerDb(t, -1, "16", "Lisa", "Lavalenet", 0, 1);
		PlayerDb p17 = new PlayerDb(t, -1, "27", "Sarah", "Halperin", 0, 1);
		PlayerDb p18 = new PlayerDb(t, -1, "58", "Meredith", "Witt", 0, 1);
		PlayerDb p19 = new PlayerDb(t, -1, "7", "Robert", "Morgan", 0, 1);
		PlayerDb p20 = new PlayerDb(t, -1, "999", "Ben", "Griessmann", 0, 1);
		PlayerDb p21 = new PlayerDb(t, -1, "998", "Sam", "Morley", 0, 1);
		PlayerDb p22 = new PlayerDb(t, -1, "77", "Alicia", "Mazurek", 0, 0);
		PlayerDb p23 = new PlayerDb(t, -1, "14", "Andrea", "Byl", 0, 0);
		PlayerDb p24 = new PlayerDb(t, -1, "37", "Eric", "Wasser", 0, 0);
		PlayerDb p25 = new PlayerDb(t, -1, "87", "Eric", "Bruch", 0, 0);
		PlayerDb p26 = new PlayerDb(t, -1, "39", "Hannah", "Katshir", 0, 0);
		PlayerDb p27 = new PlayerDb(t, -1, "9", "Jenie", "Lewandowski", 0, 0);
		PlayerDb p28 = new PlayerDb(t, -1, "33", "Jeremy", "Meeder", 0, 0);
		PlayerDb p29 = new PlayerDb(t, -1, "34", "Laurel", "Fricker", 0, 0);
		PlayerDb p30 = new PlayerDb(t, -1, "20", "Manoj", "Kowshik", 0, 0);
		PlayerDb p31 = new PlayerDb(t, -1, "713", "Matt", "Waugh", 0, 0);
		PlayerDb p32 = new PlayerDb(t, -1, "11", "Meaghan", "O'Connell", 0, 0);
		PlayerDb p33 = new PlayerDb(t, -1, "43", "Meghan", "Diehl", 0, 0);
		PlayerDb p34 = new PlayerDb(t, -1, "17", "Natalie", "Friess", 0, 0);
		PlayerDb p35 = new PlayerDb(t, -1, "317", "Nate", "Eggleston", 0, 0);
		PlayerDb p36 = new PlayerDb(t, -1, "909", "Neil", "Syal", 0, 0);
		PlayerDb p37 = new PlayerDb(t, -1, "29", "Nolan", "Sullivan", 0, 0);
		PlayerDb p38 = new PlayerDb(t, -1, "99", "Nugget", "Wing", 0, 0);
		PlayerDb p39 = new PlayerDb(t, -1, "23", "Rita", "Morris", 0, 0);
		PlayerDb p40 = new PlayerDb(t, -1, "7", "Sam", "Whaley", 0, 0);
		PlayerDb p41 = new PlayerDb(t, -1, "32", "Zach", "Fogel", 0, 0);
		
		db.addPlayer(p);
		db.addPlayer(p1);
		db.addPlayer(p2);
		db.addPlayer(p3);
		db.addPlayer(p4);
		db.addPlayer(p5);
		db.addPlayer(p6);
		db.addPlayer(p7);
		db.addPlayer(p8);
		db.addPlayer(p9);
		db.addPlayer(p10);
		db.addPlayer(p11);
		db.addPlayer(p12);
		db.addPlayer(p13);
		db.addPlayer(p14);
		db.addPlayer(p15);
		db.addPlayer(p16);
		db.addPlayer(p17);
		db.addPlayer(p18);
		db.addPlayer(p19);
		db.addPlayer(p20);
		db.addPlayer(p21);
		db.addPlayer(p22);
		db.addPlayer(p23);
		db.addPlayer(p24);
		db.addPlayer(p25);
		db.addPlayer(p26);
		db.addPlayer(p27);
		db.addPlayer(p28);
		db.addPlayer(p29);
		db.addPlayer(p30);
		db.addPlayer(p31);
		db.addPlayer(p32);
		db.addPlayer(p33);
		db.addPlayer(p34);
		db.addPlayer(p35);
		db.addPlayer(p36);
		db.addPlayer(p37);
		db.addPlayer(p38);
		db.addPlayer(p39);
		db.addPlayer(p40);
		db.addPlayer(p41);
	}
}
