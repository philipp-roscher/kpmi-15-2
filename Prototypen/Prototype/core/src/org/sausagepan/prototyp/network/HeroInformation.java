package org.sausagepan.prototyp.network;

import org.sausagepan.prototyp.model.Status;
import org.sausagepan.prototyp.model.Weapon;

public class HeroInformation {
	public String clientClass;

	public HeroInformation() { }
	
	public HeroInformation(String clientClass) {
		this.clientClass = clientClass;
	}
	
	/*public String name;
	public String sex;
	public String spriteSheet;
	public Status status;
	public Weapon weapon;
	
	public HeroInformation() { }
	
	public HeroInformation(String name, String sex, String spriteSheet, Status status, Weapon weapon) {
		this.name = name;
		if(!sex.equals("m") && !sex.equals("f")) throw new IllegalArgumentException();
		this.sex = sex;
		this.status = status;
		this.weapon = weapon;
		this.spriteSheet = spriteSheet;		
	}*/
}
