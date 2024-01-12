package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Dto.SaveMessageDTO;
import com.korea.project2_team4.Model.Entity.SaveMessage;
import com.korea.project2_team4.Repository.SaveMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveMessageDTOService {
    private final SaveMessageRepository saveMessageRepository;

    public List<SaveMessageDTO> getMyDMList(String myName, Long dmPageId) {
        List<SaveMessageDTO> mylist = new ArrayList<>();
        List<SaveMessage> beforeDTOlist = saveMessageRepository.findAllByNameAnddmPageId(myName , dmPageId);
        for (SaveMessage s : beforeDTOlist) {
            SaveMessageDTO d = new SaveMessageDTO();
            d.setAuthor(s.getAuthor());
            d.setReceiver(s.getReceiver());
            d.setContent(s.getContent());
            d.setCreateDate(s.getCreateDate());

            mylist.add(d);
        }
        return mylist;
    }

//    public List<SaveMessageDTO> getYourDMList(String partnerName, Long dmPageId) {
//        List<SaveMessageDTO> yourlist = new ArrayList<>();
//        List<SaveMessage> beforeDTOlist = saveMessageRepository.findAllByNameAnddmPageId(partnerName , dmPageId);
//        for (SaveMessage s : beforeDTOlist) {
//            SaveMessageDTO d = new SaveMessageDTO();
//            d.setAuthor(s.getAuthor());
//            d.setReceiver(s.getReceiver());
//            d.setContent(s.getContent());
//            d.setCreateDate(s.getCreateDate());
//
//            yourlist.add(d);
//        }
//        return yourlist;
//    }
}
