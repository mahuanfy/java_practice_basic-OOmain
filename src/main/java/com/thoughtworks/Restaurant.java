package com.thoughtworks;

import java.util.ArrayList;

public class Restaurant {

  public String bestCharge(String selectedItems) {
    ArrayList<Dish> menu = new MenuParser(selectedItems).getMenuList();
    int[] subtotal = getSubtotal(menu);
    String strategy = getStrategy(menu, subtotal);
    return new OrderRender(menu, subtotal, strategy).renderReceipt();
  }

  public int[] getSubtotal(ArrayList<Dish> menu) {
    int[] subtotal = new int[menu.size()];
    for (int i = 0; i < subtotal.length; i++) {
      subtotal[i] = menu.get(i).getCount() * (int) menu.get(i).getPrice();
    }
    return subtotal;
  }

  public String getStrategy(ArrayList<Dish> menu, int[] subtotal) {
    ArrayList<Integer> halfOffInfo = calculateHalfOff(menu);
    int total = calculateTotal(subtotal);
    int discount = calculateFullOff(total);
    StringBuilder strategy = new StringBuilder();
    if (discount > 0) {
      strategy.append("-----------------------------------\n").append("使用优惠:\n");
      if (discount < halfOffInfo.get(halfOffInfo.size() - 1)) {
        total -= halfOffInfo.get(halfOffInfo.size() - 1);
        strategy.append("指定菜品半价(");
        int infoLen = halfOffInfo.size();
        for (int i = 0; i < infoLen - 1; i++) {
          strategy.append(menu.get(halfOffInfo.get(i)).getName());
          if (i != infoLen - 2) {
            strategy.append("，");
          }
        }
        strategy.append(")，省").append(halfOffInfo.get(infoLen - 1)).append("元\n");
      } else {
        total -= discount;
        strategy.append("满30减6元，省6元\n");
      }
    }
    strategy.append("-----------------------------------\n总计：").append(total).append("元\n");
    return strategy.toString();
  }

  public int calculateTotal(int[] subtotal) {
    int total = 0;
    for (int i: subtotal) {
      total += i;
    }
    return total;
  }

  public ArrayList<Integer> calculateHalfOff(ArrayList<Dish> menu) {
    ArrayList<Integer> halfOffInfo = new ArrayList<>();
    int reducePrice = 0;
    for (int i = 0; i < menu.size(); i++) {
      Dish dish = menu.get(i);
      if(DataProvider.getHalfDishIds().contains(dish.getId())) {
        reducePrice += (int)dish.getPrice() / 2;
        halfOffInfo.add(i);
      }
    }
    halfOffInfo.add(reducePrice);
    return halfOffInfo;
  }

  public int calculateFullOff(int total) {
    int reducePrice = 0;
    if (30 <= total) {
      reducePrice = 6;
    }
    return reducePrice;
  }
}
