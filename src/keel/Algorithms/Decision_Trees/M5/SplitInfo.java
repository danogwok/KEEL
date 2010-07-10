/**
* <p>
* @author Written by Cristobal Romero (Universidad de C�rdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

import java.io.*;
import java.util.*;

/**
 * Stores split information.
 */
public final class SplitInfo implements Serializable {

    int number; // number of total instances
    int first; // first instance index
    int last; // last instance index
    int position; // position of maximum impurity reduction
    double maxImpurity; // maximum impurity reduction
    double leftAve; // left average class value
    double rightAve; // right average class value
    int splitAttr; // spliting attribute
    double splitValue; // splitting value

    /**
     * Constructs an object which contains the split information
     * @param low the index of the first instance
     * @param high the index of the last instance
     * @param attr an attribute
     */
    public SplitInfo(int low, int high, int attr) {
        number = high - low + 1;
        first = low;
        last = high;
        position = -1;
        maxImpurity = -1.e20;
        splitAttr = attr; // attr < 0 is an empty object
        splitValue = 0.0;
    }

    /**
     * Makes a copy of this SplitInfo object
     */
    public final SplitInfo copy() {

        SplitInfo s = new SplitInfo(first, last, -1);

        s.number = number;
        s.first = first;
        s.last = last;
        s.position = position;
        s.maxImpurity = maxImpurity;
        s.leftAve = leftAve;
        s.rightAve = rightAve;
        s.splitAttr = splitAttr;
        s.splitValue = splitValue;

        return s;
    }

    /**
     * Resets the object of split information
     * @param low the index of the first instance
     * @param high the index of the last instance
     * @param attr the attribute
     */
    public final void initialize(int low, int high, int attr) {

        number = high - low + 1;
        first = low;
        last = high;
        position = -1;
        maxImpurity = -1.e20;
        splitAttr = attr;
        splitValue = 0.0;
    }

    /**
     * Converts the spliting information to string
     * @param inst the instances
     */
    public final String toString(M5Instances inst) {

        StringBuffer text = new StringBuffer();

        text.append("Print SplitInfo:\n");
        text.append("    Instances:\t\t" + number + " (" + first + "-" +
                    position + "," + (position + 1) + "-" + last + ")\n");
        text.append("    Maximum Impurity Reduction:\t" +
                    M5.doubleToStringG(maxImpurity, 1, 4) + "\n");
        text.append("    Left average:\t" + leftAve + "\n");
        text.append("    Right average:\t" + rightAve + "\n");
        if (maxImpurity > 0.0) {
            text.append("    Splitting function:\t" +
                        inst.attribute(splitAttr).name() + " = " + splitValue +
                        "\n");
        } else {
            text.append("    Splitting function:\tnull\n");
        }

        return text.toString();
    }

    /**
     * Finds the best splitting point for an attribute in the instances
     * @param attr the splitting attribute
     * @param inst the instances
     * @exception Exception if something goes wrong
     */
    public final void attrSplit(int attr, M5Instances inst) throws Exception {
        int i, len, count, part;
        Impurity imp;

        int low = 0;
        int high = inst.numInstances() - 1;
        this.initialize(low, high, attr);
        if (number < 4) {
            return;
        }

        len = ((high - low + 1) < 5) ? 1 : (high - low + 1) / 5;
        /* if(len>25)len=25; */
        //    len=1;

        position = low;

        part = low + len - 1;
        imp = new Impurity(part, attr, inst, 5);

        count = 0;
        for (i = low + len; i <= high - len - 1; i++) {

            imp.incremental(inst.instance(i).classValue(), 1);

            if (M5.eqDouble(inst.instance(i + 1).value(attr),
                            inst.instance(i).value(attr)) == false) {
                count = i;
                if (imp.impurity > maxImpurity) {
                    maxImpurity = imp.impurity;
                    splitValue = (inst.instance(i).value(attr) +
                                  inst.instance(i + 1).value(attr)) * 0.5;
                    leftAve = imp.sl / imp.nl;
                    rightAve = imp.sr / imp.nr;
                    position = i;
                }
            }
        }
    }

}