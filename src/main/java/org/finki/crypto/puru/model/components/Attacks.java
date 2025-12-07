package org.finki.crypto.puru.model.components;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Attacks {
    private List<Location> locations = new ArrayList<>();
}
