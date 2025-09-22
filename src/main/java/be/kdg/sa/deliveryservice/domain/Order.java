package be.kdg.sa.deliveryservice.domain;

public class Order {
    private final OrderId orderId;
    private String name;
    private String contactEmail;
    private Address address;
    private OrderStatus status;

    public Order(OrderId orderId, String name, String contactEmail, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.contactEmail = contactEmail;
        this.address = address;
        this.status = OrderStatus.ACCEPTED;
    }

    public static Order place(final String name, final String contactEmail, final Address address) {
        final OrderId orderId = OrderId.create();
        return new Order(orderId, name, contactEmail, address);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public Address getAddress() {
        return address;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
