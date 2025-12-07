package org.finki.crypto.puru.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Server {
    private List<PlayerInfo> playerInfoList;
}
