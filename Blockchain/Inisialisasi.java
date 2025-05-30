package Blockchain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Inisialisasi {

    public static final String[] names = {
        "Halie Fierro", "Garett Kirkland", "Lina Wilks", "Gannon Blair", "Shawn McAdams",
        "Grayson Yazzie", "Dayanara Roark", "Julieta Rushing", "Lena Hoffmann", "Nicklaus Delossantos",
        "Marlena Rinehart", "Mason Lofton", "Dequan Lawson", "Celina Donnelly", "Luna Birch",
        "Monique Skipper", "Bernadette Shaffer", "Myron Strange", "Jayda Smallwood", "Rena Flynn",
        "Yolanda Jewell", "Anna Rawlings", "Cheryl Artis", "Terra Yanez", "Sana Hamblin",
        "Brianna Parry", "Marcellus Tavares", "Joslyn Clark", "Theresa France", "Verania Hoyle",
        "Jude Hickey", "Terra Broughton", "Caroline Cook", "Mira Blum", "Marcanthony Mota",
        "Hugo Carmona", "Sonia Hussain", "Emelia Connell", "Maximus Guthrie", "Zane Paine",
        "Sommer Carlin", "Rowan Lenz", "Raven Thigpen", "Gaven Neely", "Finn Tamayo",
        "Nikolas Blakely", "Justus May", "Ali Mason", "Deanna Barone", "Cherokee Lance",
        "Stefany Saavedra", "Francesca Frederick", "Fiona Colvin", "Jericho Allan", "Waylon McClure",
        "Kenneth Weston", "Dorothy Whitten", "Camila Stephen", "Domenic German", "Mckayla Arnett",
        "Terrance Turley", "Jamie Streeter", "Destany Clancy", "Camden Lawrence", "Annemarie Dewitt",
        "Kendall Wing", "Blanca Ramos", "Reuben Richardson", "Macy Valdez", "Erich Scruggs",
        "Jessi Unger", "Chantel Ainsworth", "Tyshawn Carver", "Maximilian Helm", "Kyndall Gentile",
        "Silvia McMahon", "Ezekiel Rizzo", "Kaya Dooley", "Travion Quintana", "Katheryn French",
        "Josue Richter", "Dale Dyson", "Kyla Wells", "Lexie Wahl", "Darrion Hurt",
        "Brenton Booker", "Skyler Garcia", "Jennie Varner", "Madeleine Avery", "Salvador Keys",
        "Valeria Carbajal", "Kelsie Childers", "Oriana German", "Greta Kane", "Jaylon Bush",
        "Alivia Grossman", "Treyvon Breen", "Neal Shell", "Braxton Stroud", "Erica Cosby",
        "Juwan Swenson", "Eleanor Le", "Adelaide Peebles", "Alex McCune", "Keegan Sweat",
        "Shania Leger", "Aidan Leavitt", "Jaret Rocha", "Hadley Seitz", "Jaleel Strong",
        "Julianna Knapp", "Jovanny Yates", "Colleen Mcgrew", "Shyann Pino", "Zack Phillips",
        "Kaleb Ash", "Colette Carr", "Jennie Steffen", "Myranda Ridgeway", "Jensen Francois",
        "Demarcus Trinh", "Cyrus Stoltzfus", "Donald McCombs", "Jamison Carson", "Dakoda Waite",
        "Marlena Ott", "Meaghan Isom", "Toby Rees", "Travis Chastain", "Phillip Seitz",
        "Laisha Maier", "Dale DeJesus", "Arturo Archuleta", "Trenton Buss", "Mandy Bain",
        "Jerome Ashworth", "Annelise Winter", "Katlin Pond", "Khalil Tamayo", "Jacinda Cassidy",
        "Gracelyn Mai", "Amaya Abney", "Rubi Jeffrey", "Regan Stephenson", "Zaire Robledo",
        "Isabel Cornelius", "Randi Vega", "Melia Peek", "Gannon Fajardo", "Stephan Larsen",
        "Rey Hawley", "Kathryn Cutler", "Casey McCurdy", "Cielo Foss", "Brennon Hancock",
        "Ervin Hsu", "Lucas Yarbrough", "Elana Arredondo", "Shaun Bingham", "Valentin Hurley",
        "Araceli Navarrete", "Emalee McNeill", "Guadalupe Pedersen", "Royce Sheldon", "Dennis Barkley",
        "Aliza Skaggs", "Fabiola Walton", "Kaylyn Reyna", "Demond Toledo", "David Russ",
        "Christion Harder", "Dario Kearney", "Aiden Everett", "Jerome Brice", "Edmund Ernst",
        "Mona Redding", "Wilfredo Huber", "Dayna Brooks", "Riya McCorkle", "Raelynn Lunsford",
        "Leigh Liang", "Mariana Burroughs", "Reina Tidwell", "Stephan Pullen", "Alena Silverman",
        "Claudio Farrow", "Trenten DeJesus", "Leona Bock", "Jaelynn Whatley", "German Etheridge",
        "Eboni Gant", "Juanita Chu", "Elsa Paul", "Preston Scanlon", "Camilla Hubbard",
        "Enoch Martel", "Jessie Alderman", "Branden Ashby", "Aysha Giordano", "Brett Jacob"
    };

    public static String[] getNames(){
        return names;
    }
    
    public static List<List<Transaction>> inisialisasi(int jumlahTransaksi) {
        List<List<Transaction>> listTransaksi = new ArrayList<>();
        for (int i = 0; i < jumlahTransaksi; i++) {
            List<Transaction> trx = new ArrayList<>();
            for (int j = 0; j < jumlahTransaksi; j++) {
                Random random = new Random();
                String sender = names[random.nextInt(names.length)];
                String receiver = names[random.nextInt(names.length)];
                int amount = ThreadLocalRandom.current().nextInt(10, 1001);
                double fee = 0.05*amount;

                //random timestamp
                long start = Instant.parse("2000-01-01T00:00:00Z").getEpochSecond();
                long end = Instant.parse("2030-12-31T23:59:59Z").getEpochSecond();
                long timeStamp = ThreadLocalRandom.current().nextLong(start, end + 1);
//                Transaction t = new Transaction(sender, receiver, amount, timeStamp, fee);
                
//                trx.add(t);
            }
            listTransaksi.add(new ArrayList<>(trx));
            trx.clear();
        }

        return listTransaksi;
    }

}
