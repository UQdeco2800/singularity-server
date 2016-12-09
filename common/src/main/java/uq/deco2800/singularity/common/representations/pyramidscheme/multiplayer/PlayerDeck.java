package uq.deco2800.singularity.common.representations.pyramidscheme.multiplayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by nick on 23/10/16.
 */
public class PlayerDeck implements Serializable {
    public String username;
    public ArrayList<Map<String, Integer>> deck;
}
