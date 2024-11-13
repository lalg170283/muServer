import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import io.muserver.MuServer;
import io.muserver.MuServerBuilder;
import io.muserver.rest.RestHandlerBuilder;

import jakarta.ws.rs.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Restaurant {

    public static Map<String, List<Reservation>> booking = new HashMap();
    private static final int TIME_SLOT = 2;

    public static void main(String[] args) {
        Booking booking = new Booking();
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider();
        MuServer server = MuServerBuilder.httpServer()
                .addHandler(
                        RestHandlerBuilder.restHandler(booking)
                                .addCustomWriter(jacksonJsonProvider)
                                .addCustomReader(jacksonJsonProvider)
                )
                .start();

        System.out.println("API example: " + server.uri().resolve("/restaurant/reservations"));
    }

    public static class Reservation {
        public String customerName;
        public TableSize tableSize;
        public String date;
        public String hour;
    }

    @Path("/restaurant")
    static class Booking {

        @GET
        @Path("/reservations/{date}")
        @Produces("application/json")
        public List<Reservation> getReservationByDay(@PathParam("date") String date) {
            return booking.get(date);//The format of the date to be received must be YYYY-MM-DD
        }

        /**
         *
         * There are no restaurant quota validations as only the reservation is made and the list of reservations per day is obtained
         */
        @POST
        @Path("/reservations/create")
        @Consumes("application/json")
        @Produces("application/json")
        public String create(Reservation reservation) {

                List<Reservation> reservations = booking.get(reservation.date);

                if (reservations!= null) {
                    reservations.add(reservation);
                } else {
                    List<Reservation> beginReservationByDay = new ArrayList<>();
                    beginReservationByDay.add(reservation);
                    booking.put(reservation.date, beginReservationByDay);
                }

                return "Reservation created for " + reservation.customerName + " with table size " + reservation.tableSize + " and date "+ reservation.date + " from "+ reservation.hour + " to " + getFinalSlotTime(reservation.hour) +" hours";
            }
        }


        private static int getFinalSlotTime(String hour) {
            return Integer.valueOf(hour) + TIME_SLOT;
        }


}