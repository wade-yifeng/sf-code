package cn.sf.redis.socket.httpxml;


import lombok.Data;

@Data
public class Order {
  private long orderNumber;
  private Customer customer;
  private Address billTo;
  private Shipping shipping;
  private Address shipTo;
  private Float total;
}