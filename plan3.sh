# Remove the passenger field and its annotations
sed -i '/@ManyToOne.*LAZY/,/private Passenger passenger;/d' src/main/java/com/flytrack/model/Booking.java
# Remove seatNumber and its annotation
sed -i '/@Column.*seat_number/,/private String seatNumber;/d' src/main/java/com/flytrack/model/Booking.java
# Add User
sed -i '/private Flight flight;/a \    @ManyToOne(fetch = FetchType.LAZY)\n    @JoinColumn(name = "user_id")\n    private User user;' src/main/java/com/flytrack/model/Booking.java
# Add passengers list
sed -i '/private Flight flight;/a \    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)\n    private java.util.List<Passenger> passengers = new java.util.ArrayList<>();' src/main/java/com/flytrack/model/Booking.java
