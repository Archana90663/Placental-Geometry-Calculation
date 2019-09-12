/*
 * This JAVA file calculates the geometrical location of the placenta within the uterus and outputs
 * the percentages of its presence. The algorithm behind it is that the program creates an xyz plot
 * of the averaged uterus voxel coordinates. Using this plot, it counts the number of placental
 * voxels above or below the average for each x,y,and z coordinate. The program reads in the voxel
 * coordinates of the segmented placenta and uterus through texts files.
 *              Outputs:
 *                  Left: Percentage of placental voxels towards the left of the uterus
 *                  Right: Percentage of placental voxels towards the right of the uterus
 *                  Superior: Percentage of placental voxels towards the top of the uterus
 *                  Inferior: Percentage of placental voxels towards the bottom of the uterus
 *                  Anterior: Percentage of placental voxels towards the front of the uterus
 *                  Posterior: Percentage of placenta voxels towards the back of the uterus
 *
 * Written by Archana Dhyani 9/4/2019
 * University of Wisconsin-Madison
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Scanner;

public class geometryCalculation {

  private HashMap<Integer, Float> p_rl;
  private HashMap<Integer, Float> p_ap;
  private HashMap<Integer, Float> p_fh;

  private HashMap<Integer, Float> u_rl;
  private HashMap<Integer, Float> u_ap;
  private HashMap<Integer, Float> u_fh;

  /**
   * This public constructor instantiates the above hash maps that would store the placenta
   * and uterus voxel coordinates from their respective text files.
   */
  public geometryCalculation() {
    this.p_rl = new HashMap<>();
    this.p_ap = new HashMap<>();
    this.p_fh = new HashMap<>();
    this.u_rl = new HashMap<>();
    this.u_ap = new HashMap<>();
    this.u_fh = new HashMap<>();
  }

  private float rl_avg; //The averaged x-axis plot using the uterus voxel coordinates
  private float ap_avg; //The averaged y-axis
  private float fh_avg; //The averaged z-axis

  /**
   * This method reads in the text file that contains the placental voxel coordinates. It then
   * stores these coordinates into hash maps.
   * @param file is the text file that contains the placental voxel coordinates
   * @throws FileNotFoundException is an exception thrown in case if a file does not exist
   */
  public void readInPlacentalSegmentation(String file) throws FileNotFoundException {
    Scanner reader = new Scanner(new File(file));
    String data;
    int i = 0;
    while (reader.hasNextLine()) {
      data = reader.nextLine();
      String[] values = data.split(", ");
      p_rl.put(i, Float.parseFloat(values[0]));
      p_fh.put(i, Float.parseFloat(values[1]));
      p_ap.put(i, Float.parseFloat(values[2]));
      i++;
    }
    reader.close();
  }

  /**
   * This method reads in the text file that contains the uterus voxel coordinates. It then
   * stores these coordinates into hash maps.
   * @param file is the text file that contains the uterus voxel coordinates
   * @throws FileNotFoundException is an exception thrown in case if a file does not exist
   */
  public void readInUterusSegmentation(String file) throws FileNotFoundException {
    Scanner reader = new Scanner(new File(file));
    String data;
    int i = 0;
    while (reader.hasNextLine()) {
      data = reader.nextLine();
      String[] values = data.split(", ");
      u_rl.put(i, Float.parseFloat(values[0]));
      u_fh.put(i, Float.parseFloat(values[1]));
      u_ap.put(i, Float.parseFloat(values[2]));
      i++;
    }
    reader.close();
  }

  /**
   * This method creates the averaged xyz plot using the uterus voxel coordinates.
   */
  private void findWholeSegAverage() {
    float sum_rl = 0;
    float sum_ap = 0;
    float sum_fh = 0;

    for (int i = 0; i < u_rl.size(); i++) {
      sum_rl += u_rl.get(i);
      sum_ap += u_ap.get(i);
      sum_fh += u_fh.get(i);
    }

    rl_avg = sum_rl / u_rl.size();
    ap_avg = sum_ap / u_ap.size();
    fh_avg = sum_fh / u_fh.size();

  }

  /**
   * This method calculates the percentages of the regions where most placental voxels are
   * prevalent. It outputs the voxels' location percentages by comparing the coordinates to the
   * averaged xyz plot.
   */
  public void calculateGeometry() {
    DecimalFormat df = new DecimalFormat("0.000");
    findWholeSegAverage();
    float left = 0;
    float right = 0;
    float top = 0;
    float bottom = 0;
    float ant = 0;
    float post = 0;

    for (int i = 0; i < p_rl.size(); i++) {
      if (p_rl.get(i) > rl_avg) {
        right++;
      }
      if (p_rl.get(i) < rl_avg) {
        left++;
      }
      if (p_ap.get(i) > ap_avg) {
        post++;
      }
      if (p_ap.get(i) < ap_avg) {
        ant++;
      }
      if (p_fh.get(i) > fh_avg) {
        top++;
      }
      if (p_fh.get(i) < fh_avg) {
        bottom++;
      }
    }


    left = (left / p_rl.size()) * 100;
    System.out.println("Left: " + df.format(left));
    right = 100 - left;
    System.out.println("Right: " + df.format(right));
    bottom = (bottom / p_fh.size()) * 100;
    System.out.println("Inferior: " + df.format(bottom));
    top = 100 - bottom;
    System.out.println("Superior: " + df.format(top));
    ant = (ant / p_ap.size()) * 100;
    System.out.println("Anterior: " + df.format(ant));
    post = 100 - ant;
    System.out.println("Posterior: " + df.format(post));
  }

  public static void main(String[] args) {
    geometryCalculation placenta = new geometryCalculation();
    try {
      placenta.readInPlacentalSegmentation("placenta.txt");
      placenta.readInUterusSegmentation("uterus.txt");
      placenta.calculateGeometry();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    }

  }

}
