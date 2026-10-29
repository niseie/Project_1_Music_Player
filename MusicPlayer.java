import java.util.*;

public class MusicPlayer {
    static class Song {
        String id;
        String title;
        String artist;

        public Song(String id, String title, String artist) {
            this.id = id;
            this.title = title;
            this.artist = artist;
        }
    }

    static class Node {
        Song song;
        Node next;
        Node prev;

        Node(Song song) {
            this.song = song;
        }
    }

    private Node head;
    private Node tail;

    private Map<String, Node> library = new HashMap<>();
    private Deque<Song> upNext = new ArrayDeque<>();
    private Deque<Song> history = new ArrayDeque<>();
    private Map<String, Integer> playCounts = new HashMap<>();

    private Song current;

    private void start(Song song) {
        current = song;
        playCounts.merge(song.id, 1, Integer::sum);
    }

    private void unlink(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        node.next = null;
        node.prev = null;
    }

    public boolean addSong(String id, String title, String artist) {
        if (library.containsKey(id)) {
            return false;
        }
        Node node = new Node(new Song(id, title, artist));
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        library.put(id, node);
        return true;
    }

    public Song findSong(String id) {
        Node node = library.get(id);
        if (node == null) {
            return null;
        }
        return node.song;
    }

    public List<String> showPlaylist() {
        List<String> titles = new ArrayList<>();
        for (Node n = head; n != null; n = n.next) {
            titles.add(n.song.title);
        }
        return titles;
    }

    // playing
    public Song play(String id) {
        Node node = library.get(id);
        if (node == null) {
            return null;
        }
        if (current != null) {
            history.push(current);
        }
        start(node.song);
        return current;
    }

    public Song nowPlaying() {
        return current;
    }

    public int playCount(String id) {
        return playCounts.getOrDefault(id, 0);
    }

    // up next
    public boolean queueLater(String id) {
        Node node = library.get(id);
        if (node == null) {
            return false;
        }
        upNext.addLast(node.song);
        return true;
    }

    public boolean queueNext(String id) {
        Node node = library.get(id);
        if (node == null) {
            return false;
        }
        upNext.addFirst(node.song);
        return true;          
    }

    public List<String> showUpNext() {
        List<String> titles = new ArrayList<>();
        for (Song s : upNext) {
            if (library.containsKey(s.id)) {
                titles.add(s.title);
            }
        }
        return titles;
    }
    public Song next() {
        Song dest = null;

        while (!upNext.isEmpty()) {
            Song s = upNext.pollFirst();
            if (library.containsKey(s.id)) {
                dest = s;
                break;
            }
        }

        if (dest == null && current != null) {
            Node node = library.get(current.id);
            if (node != null && node.next != null) {
                dest = node.next.song;
            }
        }
        if (dest == null) {
            return null;
        }
        if (current != null) {
            history.push(current); 
        }
        start(dest);
        return current;
    }
    public Song back() {
        Song dest = null;
        while (!history.isEmpty()) {
            Song s = history.pop();
            if (library.containsKey(s.id)) {
                dest = s;
                break;
            }
        }
        if (dest == null) {
            return null;
        }
        if (current != null) {
            upNext.addFirst(current);
        }
        start(dest);
        return current;
    }
    public List<String> showHistory() {
        List<String> titles = new ArrayList<>();
        for (Song s : history) {
            if (library.containsKey(s.id)) {
                titles.add(s.title);
            }
        }
        return titles;
    }
    //remove
    public boolean removeSong(String id) {
        Node node = library.get(id);
        if (node == null) {
            return false;
        }
        if (current != null && current.id.equals(id)) {
            return false;
        }
        unlink(node);
        library.remove(id);
        playCounts.remove(id);
        return true;
    }
    static String title(Song s) {
        return (s == null) ? "none" : s.title;
    }
    public static void main(String[] args) {
        MusicPlayer player = new MusicPlayer();

        player.addSong("Song_1", "Shot For Me", "Drake");
        player.addSong("Song_2", "Nights", "Frank Ocean");
        player.addSong("Song_3", "Holy Grail", "Jay-Z feat Justin Timberlake");
        player.addSong("Song_4", "Like A Tattoo", "Sade");
        System.out.println("Playlist: " + player.showPlaylist()); 

        System.out.println("Duplicate add: " + player.addSong("Song_2", "Dupe", "X"));
        System.out.println("Find Song_3: " + title(player.findSong("Song_3")));

        System.out.println("\nplay Song_1 -> " + title(player.play("Song_1")));
        System.out.println("next    -> " + title(player.next()));
        System.out.println("next    -> " + title(player.next()));
        System.out.println("History: " + player.showHistory());

        player.queueLater("Song_4");
        player.queueNext("Song_1");
        System.out.println("\nUp next: " + player.showUpNext());
        System.out.println("next    -> " + title(player.next()));

        System.out.println("back    -> " + title(player.back()));
        System.out.println("Up next: " + player.showUpNext());
        System.out.println("History: " + player.showHistory());

        System.out.println("\nRemove Song_4: " + player.removeSong("Song_4"));
        System.out.println("Remove playing song (Song_3): " + player.removeSong("Song_3"));
        System.out.println("Playlist: " + player.showPlaylist());

        System.out.println("\nNow playing: " + title(player.nowPlaying()));
        System.out.println("Play count Song_1: " + player.playCount("Song_1"));
        System.out.println("Play count Song_3: " + player.playCount("Song_3"));
    }
}