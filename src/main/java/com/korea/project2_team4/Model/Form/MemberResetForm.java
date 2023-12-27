package com.korea.project2_team4.Model.Form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResetForm {

    @Size(min=8, message = "비밀번호는 8자 이상 입력해 주세요.")
    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password;


    @Size(min=8, message = "비밀번호는 8자 이상 입력해 주세요.")
    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String new_password;

    @Size(min=8, message = "비밀번호는 8자 이상 입력해 주세요.")
    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String re_password;

}
