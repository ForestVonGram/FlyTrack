# 1. Update Booking.java
sed -i 's/.*private Passenger passenger;//' src/main/java/com/flytrack/model/Booking.java
sed -i 's/.*private String seatNumber;//' src/main/java/com/flytrack/model/Booking.java
sed -i '/import java.util.List;/! b; /import java.util.List;/ a import java.util.ArrayList;' src/main/java/com/flytrack/model/Booking.java
# Add OneToMany passengers instead.
sed -i '/private Flight flight;/a \    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)\n    private java.util.List<Passenger> passengers = new java.util.ArrayList<>();' src/main/java/com/flytrack/model/Booking.java
