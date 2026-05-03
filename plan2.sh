# Remove the old OneToMany from passenger to booking
sed -i '/@OneToMany(mappedBy = "passenger"/,+2d' src/main/java/com/flytrack/model/Passenger.java
# Add booking, seatNumber, baggages to Passenger
sed -i '/private String phoneNumber;/a \    @ManyToOne(fetch = FetchType.LAZY)\n    @JoinColumn(name = "booking_id")\n    private Booking booking;\n\n    @Column(name = "seat_number", length = 10)\n    private String seatNumber;\n\n    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, orphanRemoval = true)\n    private java.util.List<Baggage> baggages = new java.util.ArrayList<>();' src/main/java/com/flytrack/model/Passenger.java
# Remove unique constraints from Passenger email and identity
sed -i 's/unique = true//g' src/main/java/com/flytrack/model/Passenger.java
# Update Baggage to map to Passenger instead of Booking
sed -i 's/private Booking booking;/private Passenger passenger;/g' src/main/java/com/flytrack/model/Baggage.java
sed -i 's/@JoinColumn(name = "booking_id", nullable = false)/@JoinColumn(name = "passenger_id", nullable = false)/g' src/main/java/com/flytrack/model/Baggage.java
