import styled, { keyframes } from "styled-components";
import Header from "../components/layout/Header";
import Footer from "../components/layout/Footer";
import HomeSlide from "../components/mainpage/HomeSlide";
import TopButton from "../components/common/buttons/TopButton";
import { BaseFlexWrapper } from "../components/styled/Wrapper";
import { useState, useEffect, useRef } from "react";
import { useDispatch } from "react-redux";
import { tokenCheck } from "../utils/tokenCheck";
import { getUserProfile } from "../store/actions/user";
import "./Home.css";

const neon_text_color = "#5904de";
const neon_border_color = "#08f";
const NeonSignAnimation = keyframes`
0%,
  19%,
  21%,
  23%,
  25%,
  54%,
  56%,
  100% {
    text-shadow: -0.2rem -0.2rem 1rem #fff, 0.2rem 0.2rem 1rem #fff,
      0 0 2rem ${neon_text_color}, 0 0 4rem ${neon_text_color},
      0 0 6rem ${neon_text_color}, 0 0 8rem ${neon_text_color},
      0 0 10rem ${neon_text_color};

    box-shadow: 0 0 0.5rem #fff, inset 0 0 0.5rem #fff,
      0 0 2rem ${neon_border_color}, inset 0 0 2rem ${neon_border_color},
      0 0 4rem ${neon_border_color}, inset 0 0 4rem ${neon_border_color};
  }

  20%,
  24%,
  55% {
    text-shadow: none;
    box-shadow: none;
  }
`;

const NeonSignTitle = styled.h1`
  font-size: 5rem;
  font-weight: 200;
  font-style: italic;
  color: #fff;
  padding: 5rem 6rem 5.5rem;
  border: 0.4rem solid #fff;
  border-radius: 2rem;
  text-transform: uppercase;
  font-family: "Monoton";
  animation: ${NeonSignAnimation} 1.5s infinite alternate;
  @media screen and (max-width: 960px) {
    font-size: 4rem;
    padding: 4rem 5rem 4.5rem;
  }
  @media screen and (max-width: 720px) {
    font-size: 3rem;
    padding: 3rem 4rem 3.5rem;
  }
  @media screen and (max-width: 580px) {
    font-size: 2rem;
    padding: 3rem 4rem 3rem;
  }
`;

const ContentWrapper = styled(BaseFlexWrapper)`
  background: ${(props) => props.background};
  height: ${(props) => props.height};
  width: 100vw;
  position: ${(props) => props.position};
  &.gradientBackground {
  }
`;

ContentWrapper.defaultProps = {
  position: "static",
};

const ImageWrapper = styled.img`
  width: 550px;
`;

const LetterWrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: ${({ ta }) => ta};
`;

const Letter = styled.p`
  font-size: ${(props) => props.size};
  font-weight: ${(props) => props.weight};
  margin-left: 2rem;
  margin-right: 2rem;
  margin-bottom: 0.5rem;
  text-align: ${(props) => props.textAlign};
  font-family: ${(props) => props.fontFamily};
  line-height: 25px;

`;

const LeftSlideContent = styled.div`
  display: none;
  flex-direction: row;
  position: absolute;
  top: ${({ top }) => top};
  width: 100vw;
  height: 40rem;
  align-items: center;
  justify-content: center;
  left: 0px;

  @media screen and (max-width: 900px) {
    flex-direction: column;
  }
`;

const RightSlideContent = styled.div`
  display: none;
  flex-direction: row;
  position: absolute;
  top: ${({ top }) => top};
  width: 100vw;
  height: 40rem;
  align-items: center;
  justify-content: center;
  right: 0px;

  @media screen and (max-width: 900px) {
    flex-direction: column;
  }
`;

const Home = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    if (tokenCheck()) {
      dispatch(getUserProfile());
    }
    window.addEventListener("scroll", updateScroll);
  });

  const [scrollPosition, setScrollPosition] = useState(0);
  const isSlide = useRef([]);

  const updateScroll = () => {
    setScrollPosition(window.scrollY); // ????????? ?????? ??????

    if (scrollPosition > 600) {
      isSlide.current[0].style.display = `flex`;
      isSlide.current[0].style.animation = `slideLeft 1.0s ease-out forwards`;
    }
    if (scrollPosition > 1250) {
      isSlide.current[1].style.display = `flex`;
      isSlide.current[1].style.animation = `slideRight 1.0s ease-out forwards`;
    }
    if (scrollPosition > 1900) {
      isSlide.current[2].style.display = `flex`;
      isSlide.current[2].style.animation = `slideLeft 1.0s ease-out forwards`;
    }
  };

  return (
    <>
      <Header position={"fixed"} location={"home"} />
      <ContentWrapper background={"#000"} height={"100vh"}>
        <NeonSignTitle>OPEN DRINKUS</NeonSignTitle>
      </ContentWrapper>
      <ContentWrapper background={"#000"} height={"20vh"} />
      <ContentWrapper
        className="gradientBackground"
        background={"#FFF"}
        height={"60vh"}
        position={"relative"}
      >
        <LeftSlideContent ref={(el) => (isSlide.current[0] = el)}>
          <ImageWrapper src={process.env.PUBLIC_URL + "/assets/room1.png"} />
          <LetterWrapper ta="right">
          <img src={process.env.PUBLIC_URL + "/assets/game/bombGif.gif"} width="150px"/>

            <Letter size={"2rem"} weight={"bold"} textAlign={"left"}>
              ?????? ?????????
            </Letter>
            <Letter size={"1.1rem"} textAlign={"left"}>
              ?????? ???????????? ?????? ????????? ????????? ?????? ??? ?????????
              <br />
              ??????????????? ????????? ????????? ??????????????? ????????? ??? ?????????
            </Letter>
          </LetterWrapper>
        </LeftSlideContent>
      </ContentWrapper>
      <ContentWrapper background={"#FFF"} height={"60vh"} position={"relative"}>
        <RightSlideContent ref={(el) => (isSlide.current[1] = el)}>
          <LetterWrapper ta="left">
            <Letter size={"2rem"} weight={"bold"} textAlign={"right"}>
              ????????????
            </Letter>
            <Letter size={"1.1rem"} textAlign={"right"}>
              ???????????? ?????? ??? ?????? ?????? ???????????????
              <br />
              ???????????? ????????? ??? ?????? ?????? ??????????????? ?????????
            </Letter>
          </LetterWrapper>
          <ImageWrapper
            src={process.env.PUBLIC_URL + "/assets/room2.png"}
          />
        </RightSlideContent>
      </ContentWrapper>
      <ContentWrapper background={"#FFF"} height={"60vh"} position={"relative"}>
        <LeftSlideContent ref={(el) => (isSlide.current[2] = el)}>
          <ImageWrapper src={process.env.PUBLIC_URL + "/assets/room3.png"} />
          <LetterWrapper ta="right">
            <Letter size={"2rem"} weight={"bold"} textAlign={"left"}>
              ?????????
            </Letter>
            <Letter size={"1.1rem"} textAlign={"left"}>
              ??? ???????????? ????????????, ?????? ????????? ???????????? ??? ??? ?????????
              <br />
              ???????????? ?????? ????????? ?????? ??? ?????????
            </Letter>
          </LetterWrapper>
        </LeftSlideContent>
      </ContentWrapper>
      <ContentWrapper height={"20vh"} />
      <HomeSlide />
      <Footer />
      <TopButton />
    </>
  );
};

export default Home;
