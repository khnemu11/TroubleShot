"use client";
import React, { useState, useRef } from "react";
import { useRouter } from "next/navigation";

import Link from "next/link";
import Image from "next/image";
import backgroundImage from "../../../public/background/loginBackground4.jpg";

import { emailCert, emailcodeCheck, signUpSubmit } from "@/api/account";
import { EmailCode } from "@/types/CommonType";

import { EmailcodeSuccess, CodeCheckSuccess, CodeCheckFail, SubmitFail, SignUpSuccess } from "../toast/notify";
import { toast } from "react-toastify";

export default function page() {
  const router = useRouter();
  const [email, setEmail] = useState<string>(""); // 이메일
  const [nickname, setNickname] = useState<string>(""); // 닉네임
  const [password, setPassword] = useState<string>(""); // 비밀번호
  const [pswVld, setPswVld] = useState<boolean>(); // 비밀번호 유효성 확인

  const [checkPassword, setCheckPassword] = useState<string>(""); // 비밀번호 확인
  const [isMatch, setIsMatch] = useState<boolean | null>(null); // 비밀번호 - 비밀번호 확인

  const [isEmailRequest, setIsEmailRequest] = useState<boolean | null>(null); // 이메일 인증 요청 성공 여부
  const [isCodeCorrect, setisCodeCorrect] = useState<boolean | null>(null); // 인증번호 확인 요청 성공 여부
  const [checkNickname, setCheckNickname] = useState<boolean | null>(null); // 닉네임 유효성 확인

  const codeRef = useRef<HTMLInputElement>(null);

  // 이메일 입력
  const handleEmailInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
  };

  // 닉네임 길이 확인
  const handleNicknameInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const nicknameValue = e.target.value;
    if (nicknameValue.length >= 2 && nicknameValue.length <= 10) {
      setCheckNickname(true);
      setNickname(nicknameValue);
      return;
    }
    setCheckNickname(false);
  };

  // 비밀번호 유효성
  const handlePasswordInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const password = e.target.value;
    if (password.length < 6) {
      setPswVld(false);
      return;
    }
    setPswVld(true);
    setPassword(e.target.value);
  };

  // 비밀번호 확인
  const handleCheckPasswordInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCheckPassword(e.target.value);
    setIsMatch(e.target.value === password);
  };

  // 인증번호 이메일로 요청
  const handleEmailCert = async () => {
    const isEmailValid = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email); // boolean
    if (isEmailValid) {
      try {
        const res = await emailCert(email);
        if (res?.success) {
          EmailcodeSuccess();
          console.log("이메일 인증 요청 성공"); // 토스트로 알림 띄우기
          // 요청에 성공했으면 코드를 입력할 칸과 코드 확인 버튼을 보여줘야 한다.
          setIsEmailRequest(true);
        }
      } catch (err) {
        console.log("Error:", err);
        setIsEmailRequest(false);
      }
    } else {
      toast.error("올바른 이메일 형식이 아닙니다.");
    }
  };

  // 인증번호 확인 요청
  const handleCode = async () => {
    const code = codeRef.current!.value; // 해당 부분 오류 여지 있음. 느낌표 없애면 string 이거나 undefined
    // 이메일 인증 처리
    const params: EmailCode = { email: email, code: code };
    try {
      const res = await emailcodeCheck(params);
      if (res?.success) {
        console.log("인증번호 확인 !");
        CodeCheckSuccess();
        setisCodeCorrect(true);
      }
    } catch (err) {
      console.log("Error:", err);
      CodeCheckFail();
      setisCodeCorrect(false);
    }
  };

  // 회원가입 요청
  const handleSubmit = async () => {
    if (!isEmailRequest || !isCodeCorrect || !isMatch || !checkNickname || !pswVld) {
      SubmitFail();
      return;
    } else {
      // 제출 로직 구현
      try {
        const res = await signUpSubmit({
          email: email,
          password: password,
          nickname: nickname,
          locale: "Korea",
        });
        if (res === "success") {
          SignUpSuccess();
          setisCodeCorrect(true);
          router.push("/login");
        }
      } catch (err) {
        SubmitFail();
      }
    }
  };

  return (
    <div className="fcc w-full h-[100vh]">
      <Image src={backgroundImage} alt="backgroundImage" className="bg-cover h-screen absolute w-full -z-10 blur-lg" />
      <div className="">
        <div className="h-[10vh]"></div>
        <div className="bg-white rounded p-10 text-center shadow-md w-[25rem]">
          <h1 className="text-3xl border-b border-main">회원 가입</h1>

          <div className="my-4 text-left">
            <label className="text-gray-900 me-2">아이디(이메일)</label>
            {isEmailRequest === false && <label className="text-sub">올바른 형식이 아닙니다.</label>}
            <div className="flex">
              <input
                type="text"
                className={`border block w-full p-2 rounded me-2 ${isEmailRequest ? "bg-[#E8F0FE]" : "bg-white"}`}
                id="TSemail"
                value={email}
                onChange={handleEmailInputChange}
                placeholder="이메일을 입력하세요."
              />
              <div
                className="bg-main fcc text-white rounded-lg w-24 p-1 cursor-pointer"
                onClick={() => {
                  handleEmailCert();
                }}
              >
                인증 요청
              </div>
            </div>
          </div>

          {isEmailRequest ? (
            <div className="my-4 text-left">
              <label className="text-gray-900 me-2">인증번호 입력</label>
              {isCodeCorrect === false && <label className="text-sub">인증번호가 옳지 않습니다.</label>}
              <div className="flex">
                <input
                  type="text"
                  className={`border block w-full p-2 rounded me-2 ${isCodeCorrect ? "bg-[#E8F0FE]" : "bg-white"}`}
                  id="certCode"
                  ref={codeRef}
                  placeholder="인증번호를 입력하세요."
                />
                <div
                  className="fcc bg-sub text-white rounded-lg w-24 p-1"
                  onClick={() => {
                    handleCode();
                  }}
                >
                  확인
                </div>
              </div>
            </div>
          ) : null}

          <div className="my-4 text-left">
            <label className="text-gray-900 me-2">닉네임</label>
            {checkNickname === false && <label className="text-sub">올바른 형식이 아닙니다.</label>}
            <input
              type="text"
              className="border block w-full p-2 rounded me-2"
              id="TSnickname"
              onChange={handleNicknameInputChange}
              placeholder="최소 2자 | 최대 10자까지 가능합니다."
            />
          </div>

          <div className="my-4 text-left">
            <label className="text-gray-900 me-2">비밀번호</label>
            {pswVld === false && <label className="text-sub">비밀번호가 너무 짧습니다.</label>}
            <input
              type="password"
              className="border block w-full p-2 rounded me-2"
              id="password"
              onChange={handlePasswordInputChange}
              placeholder="6자 이상 입력 해주세요."
            />
          </div>

          <div className="my-4 text-left">
            <label className="text-gray-900 me-2">비밀번호 확인</label>
            {isMatch === false && <label className="text-sub">비밀번호가 옳지 않습니다.</label>}
            <input
              type="password"
              className="border block w-full p-2 rounded me-2"
              id="passwordCheck"
              onChange={handleCheckPasswordInputChange}
              placeholder="비밀번호 확인입니다."
            />
          </div>

          <div id="submitBox" className="flex justify-around mt-6 mb-4">
            <Link href={"/login"} className="bg-slate-400 text-white py-2 inline-block w-5/12 rounded-lg">
              뒤로가기
            </Link>

            <button
              className="bg-main text-white py-2 inline-block w-5/12 rounded-lg cursor-pointer"
              onClick={handleSubmit}
            >
              가입하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
