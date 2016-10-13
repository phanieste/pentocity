package pentos.g5;

import pentos.sim.Cell;
import pentos.sim.Building;
import pentos.sim.Land;
import pentos.sim.Move;

import pentos.g5.util.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Properties;

import pentos.g5.util.Pentominos;


public class Sequencer implements pentos.sim.Sequencer {

    private String residences = "Ff";
    private int[] factories_l = {5,1};
    private int[] factories_b = {5,1};
    private double prob_factories = 0.5;
    private Random gen;
    private int i=0;
    
    public void init () {
        gen = new Random();
    }
    
    private Building factoryOfSize(int width, int height) { // random rectangle with side length between 2 and 5 inclusive
    Set<Cell> factory = new HashSet<Cell>();
	for (int i=0; i<width; i++) {
	    for (int j=0; j<height; j++) {
		factory.add(new Cell(i,j));
	    }
	}
	return new Building(factory.toArray(new Cell[factory.size()]), Building.Type.FACTORY);
    }
    
    public Building next() {
        i = (i+1) % 2;
        if (gen.nextDouble() > prob_factories ) {
            return Pentominos.byName(residences.toCharArray()[i]);
        } else {
            return factoryOfSize(factories_l[i], factories_b[i]);
        }
    }
}
