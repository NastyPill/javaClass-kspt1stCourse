package longArithm;

import java.util.ArrayList;

public class DecimalNumber {

    private static final int BETA = 10000;

    private String fractionalPart;
    private String integerPart;
    /*
     *  full representation of number
     *  contains: intPart + "." + fractPart
     */
    private StringBuilder number;

    private Boolean isInt = false;
    private Boolean moreThanOne = false;

    /*
     *   Construct Decimal number from two parts
     *   which are strings
     */

    public DecimalNumber(String integerPart, String fractionalPart) {
        this.fractionalPart = fractionalPart;
        this.integerPart = integerPart;
        setNumber();
    }

    public DecimalNumber() {

    }

    //  Construct Decimal number from primitive

    public DecimalNumber(Number number) {
        setPrimitive(number);
        setNumber();
    }

    //  Construct Decimal number from string

    public DecimalNumber(String number) {
        this.number = new StringBuilder(number);
        if (number.contains(".")) {
            String[] s = number.split("\\.");
            integerPart = s[0];
            fractionalPart = s[1];
        } else {
            integerPart = number;
            fractionalPart = null;
        }
    }

    private void setPrimitive(Number number) {
        if (number instanceof Long) {
            integerPart = Long.toString(number.longValue());
        }
        if (number instanceof Integer) {
            integerPart = Integer.toString(number.intValue());
        }
        if (number instanceof Float) {
            String[] arr = Float.toString(number.floatValue()).split("\\.");
            integerPart = arr[0];
            fractionalPart = arr[1];
        }
        if (number instanceof Double) {
            String[] arr = Double.toString(number.doubleValue()).split("\\.");
            integerPart = arr[0];
            fractionalPart = arr[1];
        }
    }

    private void setNumber() {
        number = new StringBuilder();
        number.append(integerPart);
        number.append(".");
        number.append(fractionalPart);
    }

    public String getNumber() {
        setNumber();
        return number.toString();
    }

    private String addZerosToString(String number, int zeros, Boolean isInt) {
        StringBuilder result = new StringBuilder();
        if (isInt) {
            for (int i = 0; i < zeros; i++) {
                result.append("0");
            }
            result.append(number);
        } else {
            result.append(number);
            for (int i = 0; i < zeros; i++) {
                result.append("0");
            }

        }
        return result.toString();
    }

    //array represents number like xxxx * 1000^i

    private int[] setPowArray(String number, int zeros, Boolean isInt) {
        StringBuilder editedNum = new StringBuilder(addZerosToString(number, zeros, isInt));
        Number n = editedNum.length();
        int size = (int) Math.ceil(n.doubleValue() / 4);
        int[] powArray = new int[size];

        for (int i = size - 1; i >= 0; i--) {
            StringBuilder digits = new StringBuilder();
            for (int j = 0; j < 4 && editedNum.length() > 0; j++) {
                digits.insert(0, editedNum.charAt(editedNum.length() - 1));
                editedNum.deleteCharAt(editedNum.length() - 1);
            }
            powArray[size - i - 1] = Integer.parseInt(digits.toString());
        }

        return powArray;
    }

    //returns a + b as an arrayList

    private ArrayList<Integer> summator(int[] thisArray, int[] otherArray) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < thisArray.length; i++) {
            int sum = thisArray[i] + otherArray[i];
            if (i == thisArray.length - 1 && sum >= BETA) {
                if (!isInt) {
                    moreThanOne = true;
                }
                result.add(sum);
            } else {
                if (sum >= BETA) {
                    thisArray[i + 1] += 1;
                    result.add(sum);
                } else {
                    result.add(sum);
                }
            }
            System.out.println(thisArray[i] + " + " + otherArray[i] + " = " + sum);
        }
        isInt = true;
        return result;
    }

    public DecimalNumber sum(DecimalNumber other, int accuracy) {
        DecimalNumber result = new DecimalNumber();
        StringBuilder resIntPart = new StringBuilder();
        StringBuilder resFractPart = new StringBuilder();

        //fill list for fractPart
        int zeros = this.fractionalPart.length() - other.fractionalPart.length();
        int[] thisArray = setPowArray(this.fractionalPart, zeros > 0 ? 0 : -zeros, false);
        int[] otherArray = setPowArray(other.fractionalPart, zeros > 0 ? zeros : 0, false);
        ArrayList<Integer> fractList = summator(thisArray, otherArray);

        //fill list for intPart
        zeros = this.integerPart.length() - other.integerPart.length();
        thisArray = setPowArray(this.integerPart, zeros > 0 ? 0 : -zeros, true);
        otherArray = setPowArray(other.integerPart, zeros > 0 ? zeros : 0, true);
        ArrayList<Integer> intList = summator(thisArray, otherArray);

        //adding int part
        for (int i = intList.size() - 1; i >= 0; i--) {
            if (moreThanOne && i == 0) {
                resIntPart.append(intList.get(i) + 1);
            } else {
                resIntPart.append(intList.get(i));
            }
            if (intList.get(i) >= BETA && intList.size() > 1) {
                resIntPart.deleteCharAt(resIntPart.length() - 5);
            }
        }

        //if there's no int part
        if (resIntPart.length() == 0) {
            if (moreThanOne) {
                resIntPart.append(1);
            } else {
                resIntPart.append(0);
            }
        }

        //adding fract part
        for (int i = fractList.size() - 1; i >= 0; i--) {
            resFractPart.append(fractList.get(i));
            if (fractList.get(i) >= BETA) {
                resFractPart.deleteCharAt(resFractPart.length() - 5);
            }
        }

        //init false every logical variable
        moreThanOne = false;
        isInt = false;
        return round(new DecimalNumber(resIntPart.toString(), resFractPart.toString()), accuracy);

    }

    public String difference(DecimalNumber other, int accuracy) {
        //TODO()
        throw new UnsupportedOperationException();
    }

    public String multiplication(DecimalNumber other, int accuracy) {
        //TODO()
        throw new UnsupportedOperationException();
    }

    private DecimalNumber round(DecimalNumber num, int accuracy) {
        StringBuilder number = new StringBuilder(num.fractionalPart);
        try {
            if (number.charAt(accuracy) < '5') {
                num.fractionalPart = number.substring(0, accuracy);
                return num;
            }

            if (number.charAt(accuracy) >= '5' && number.charAt(accuracy - 1) < '9') {
                char c = (char) (number.charAt(accuracy - 1) + 1);
                number.replace(accuracy - 1, accuracy, Character.toString(c));
                num.fractionalPart = number.substring(0, accuracy);
                return num;
            } else {
                Boolean additionalDigit = false;
                Boolean posShift = false;
                int lengthOfInt = num.integerPart.length();
                int position = accuracy + num.integerPart.length();
                number = new StringBuilder(num.integerPart + num.fractionalPart);

                while (true) {
                    if (number.charAt(position - 1) == '9' || posShift) {
                        posShift = false;
                        if (position > 1) {
                            // xxx.xxx9 -> xxx.xx(x+1)0
                            number.replace(position - 1, position, "0");
                            if (number.charAt(position - 2) == '9') {
                                posShift = true;
                                number.replace(position - 2, position - 1, "0");
                            } else {
                                char c = (char) (number.charAt(position - 2) + 1);
                                number.replace(position - 2, position - 1, Character.toString(c));
                            }
                        } else {
                            additionalDigit = true;
                            number.insert(0, '1');
                            break;
                        }
                    } else {
                        break;
                    }
                    position--;
                }

                if (additionalDigit) {
                    lengthOfInt++;
                }

                num.integerPart = number.substring(0, lengthOfInt);
                num.fractionalPart = number.substring(lengthOfInt, lengthOfInt + accuracy);
                return num;
            }
        } catch (StringIndexOutOfBoundsException ex) {
            System.err.println("Accuracy value: " + accuracy + " is illegal, because of number's length");
            throw ex;
        }
    }

    public Number getStandartType(DecimalNumber number, standartTypes type) {
        switch (type) {
            case INT: {

            }
            case LONG: {

            }
            case DOUBLE: {

            }
            case FLOAT: {

            }
            default:
                throw new IllegalArgumentException();
        }
    }
}