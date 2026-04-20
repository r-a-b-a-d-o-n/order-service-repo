package com.example.orderservice.consumer;

import java.util.Arrays;

class Solution {
    public static void main(String[] args) {
        int[] nums = new int[]{0, 1, 0, 3, 12};
        String s = "abc";
        String t = "ahbgdc";
        //System.out.println(isSubsequence(s,t));
        int[] numbers = new int[]{4,1,2,1,2};

        System.out.println(singleNumber(numbers));
       // Arrays.stream(moveZeroes(nums)).forEach(x->System.out.println(x));

    }

    public static int[] moveZeroes(int[] nums) {
        /*Input: nums = [0,1,0,3,12]
        Output: [1,3,12,0,0]*/


        int i = 0, j = 0, aux;
        while (i < nums.length && nums.length>1) {
            while ( j< nums.length && nums[j] == 0 ) {
                j++;
            }
            if(j ==  nums.length){
                break;
            }
            aux = nums[j];
            nums[j] = 0;
            nums[i] = aux;
            i++;
            j++;
        }
        return nums;
    }
        public static boolean isSubsequence(String s, String t) {
//            String s = "aabc";
//            String t = "aabgdc";
            int max = -1;

            for (int i=0; i<s.length();i++){

                int indexOfSubString = t.indexOf(s.charAt(i),max+1);
                if(indexOfSubString > max)
                {
                    max = indexOfSubString;

                }
                else return false;
            }
            return true;


    }
    public static int singleNumber(int[] nums) {
return Arrays.stream(nums).distinct().findFirst().getAsInt();
//        int found = 0;
//        for (int i =0; i<nums.length;i++){
//            int j = 0;
//            while(j<nums.length)
//            {
//                if(nums[i] == nums[j] && i!=j){
//                    break;
//                }
//
//                if(j == nums.length-1)
//                {
//                    found = i;
//                }
//                j++;
//
//            }
//
//        }
//        return nums[found];

    }

}
