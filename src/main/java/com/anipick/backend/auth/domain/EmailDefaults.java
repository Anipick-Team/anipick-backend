package com.anipick.backend.auth.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailDefaults {
    public static final String EMAIL_VERIFICATION_SUBJECT = "Anipick 이메일 인증";
    public static final String EMAIL_FORMAT_HTML = """
            <div style="font-family: 'Apple SD Gothic Neo', sans-serif; max-width: 480px; margin: auto; border: 1px solid #eee; padding: 20px;">
                            <div style="text-align: center; padding: 20px 0;">
                                <img src="https://choiseowoong.github.io/anipick-assets/images/anipick_email_verify_image.png" alt="AniPick" style="width: 100%%; max-width: 662px; height: auto; display: block; margin: 0 auto;">
                            </div>
                            <div style="background-color: #f9f9f9; padding: 30px; border-radius: 10px;">
                                <h2 style="text-align: center; color: #1b1b1b;">Anipick 이메일 인증</h2>
                                <p style="text-align: center; font-size: 15px; color: #333;">
                                    Anipick 이메일 인증 번호가 발급되었습니다.<br>
                                    비밀번호를 변경하고 싶지 않거나 요청하지 않으신 경우,<br>
                                    본 메일은 무시해주세요.
                                </p>
                                <div style="background: #fff; border: 1px solid #ddd; border-radius: 10px; padding: 20px; margin: 30px 0; text-align: center;">
                                    <p style="margin: 0; font-size: 14px; color: #333;">가입 중인 화면에 아래의 인증번호를 입력해주세요.</p>
                                    <p style="margin: 10px 0 0; font-size: 24px; font-weight: bold; color: #1b1b1b;">%s</p>
                                </div>
                                <p style="font-size: 12px; text-align: center; color: #888;">본 메일은 발송 전용이오니 문의사항이 있는 경우 <a href="mailto:teamanipick@gmail.com" style="color: #3366cc; text-decoration: none;">고객 문의 이메일(teamanipick@gmail.com)</a>로 문의 부탁드립니다.</p>
                            </div>
                        </div>
            """;
    public static final String SENDER_EMAIL_KEY = "senderEmail:";
}
