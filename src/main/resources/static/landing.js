(() => {
  'use strict';

  const ready = (callback) => {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', callback, { once: true });
    } else {
      callback();
    }
  };

  ready(() => {
    const body = document.body;
    const statusElement = document.getElementById('deep-link-status');

    // 딥링크 스킴 & 안드로이드 패키지명
    const DEEP_LINK_SCHEME = 'anipick://app/anime/detail/';
    const ANDROID_PACKAGE_NAME = 'com.jparkbro.anipick'; // 실제 패키지명으로 맞게 수정

    const animeId = getAnimeIdFromPath();
    const deepLinkUrl = animeId ? `${DEEP_LINK_SCHEME}${animeId}` : '';

    const storeUrls = {
      ios: (body.dataset.iosStore || '').trim(),
      android: (body.dataset.androidStore || '').trim()
    };

    const uaRaw = navigator.userAgent || navigator.vendor || window.opera || '';
    const ua = uaRaw.toLowerCase();
    const isAndroid = ua.includes('android');
    const isIOS =
        /iphone|ipad|ipod/i.test(uaRaw) ||
        (uaRaw.includes('Macintosh') && 'ontouchend' in document);

    const androidIntentUrl = animeId
        ? `intent://app/anime/detail/${encodeURIComponent(
            animeId
        )}#Intent;scheme=anipick;package=${ANDROID_PACKAGE_NAME};end`
        : '';

    if (!deepLinkUrl) {
      handleMissingAnimeId();
      return;
    }

    const platform = detectPlatform();
    const storeButtons = document.querySelectorAll('.store-button');

    decorateStoreButtons(storeButtons, storeUrls, platform);
    bindStoreButtons(storeButtons, storeUrls);

    updateHelperCopy(platform);

    // 페이지 로드되면 자동으로 앱 열기 시도
    window.addEventListener(
        'load',
        () => {
          // 약간의 딜레이 후 자동 시도
          setTimeout(() => attemptDeepLink(), 300);
        },
        { once: true }
    );

    // ==========================
    //   내부 함수들
    // ==========================

    function attemptDeepLink() {
      announceStatus('앱을 여는 중입니다...');

      // PC 환경이면 스토어 웹 페이지로만 보내기
      if (!isAndroid && !isIOS) {
        if (storeUrls.android) {
          announceStatus('PC 환경입니다. Google Play 스토어 페이지로 이동합니다.');
          window.location.href = storeUrls.android;
        } else {
          announceStatus('PC 환경에서는 모바일 기기로 접속해 주세요.');
        }
        return;
      }

      const fallbackUrl = isIOS ? storeUrls.ios : storeUrls.android;
      const fallbackName = isIOS ? 'App Store' : 'Google Play 스토어';

      // 🔁 타임아웃 후 스토어로 이동 (앱이 설치되어 있어서 딥링크가 성공하면,
      // 페이지 자체가 사라지기 때문에 이 타이머는 실행되지 않음)
      let fallbackTimer = null;
      if (fallbackUrl) {
        fallbackTimer = setTimeout(() => {
          announceStatus(`앱이 열리지 않아 ${fallbackName}로 이동합니다.`);
          window.location.href = fallbackUrl;
        }, 1500);
      }

      // 👉 플랫폼별 딥링크 시도
      try {
        if (isAndroid) {
          // 안드로이드: intent:// 우선 시도 (카카오톡 등 인앱 브라우저 호환 ↑)
          if (androidIntentUrl) {
            window.location.href = androidIntentUrl;
          } else {
            window.location.href = deepLinkUrl;
          }
        } else if (isIOS) {
          // iOS: 커스텀 스킴 직접 호출
          window.location.href = deepLinkUrl;
        }
      } catch (e) {
        console.warn('[landing] deep link error', e);
        // 예외가 나면 즉시 스토어로 보냄
        if (fallbackUrl) {
          clearTimeout(fallbackTimer);
          window.location.href = fallbackUrl;
        }
      }
    }

    function updateHelperCopy(currentPlatform) {
      const platformText =
          currentPlatform === 'ios'
              ? 'iOS 기기에서 접속하셨습니다.'
              : currentPlatform === 'android'
                  ? 'Android 기기에서 접속하셨습니다.'
                  : '접속하신 기기를 확인 중입니다.';

      const fallbackText =
          currentPlatform === 'ios'
              ? '앱이 설치되어 있다면 곧바로 앱이 열리고, 설치되어 있지 않다면 App Store로 이동합니다.'
              : currentPlatform === 'android'
                  ? '앱이 설치되어 있다면 곧바로 앱이 열리고, 설치되어 있지 않다면 Google Play 스토어로 이동합니다.'
                  : '앱이 열리지 않는다면 사용 중인 기기에 맞는 스토어 버튼을 눌러 설치해 주세요.';

      announceStatus(`${platformText}<br>${fallbackText}`);
    }

    function announceStatus(message) {
      if (!statusElement) return;
      statusElement.innerHTML = message;
    }

    function handleMissingAnimeId() {
      announceStatus('애니 정보를 확인할 수 없어 앱 열기를 건너뜁니다.');
      const openAppButton = document.querySelector('[data-open-app]');
      if (openAppButton) {
        openAppButton.disabled = true;
        openAppButton.setAttribute('aria-disabled', 'true');
      }
    }

    function decorateStoreButtons(buttons, urls, currentPlatform) {
      buttons.forEach((button) => {
        const target = normalizePlatform(button.dataset.platform);
        if (!target) return;

        if (!urls[target]) {
          button.disabled = true;
          button.setAttribute('aria-disabled', 'true');
        }

        if (target === currentPlatform) {
          button.classList.add('is-active');
        }
      });
    }

    function bindStoreButtons(buttons, urls) {
      buttons.forEach((button) => {
        const target = normalizePlatform(button.dataset.platform);
        if (!target || !urls[target]) return;

        button.addEventListener('click', () => {
          window.location.href = urls[target];
        });
      });
    }

    function detectPlatform() {
      if (isAndroid) return 'android';
      if (isIOS) return 'ios';
      return 'unknown';
    }

    function normalizePlatform(value) {
      if (!value) return null;
      const lowered = value.toString().toLowerCase();
      if (lowered === 'ios' || lowered === 'apple') return 'ios';
      if (lowered === 'android') return 'android';
      return null;
    }

    function getAnimeIdFromPath() {
      // 예: /app/anime/detail/12345 → 12345
      const match = window.location.pathname.match(/\/app\/anime\/detail\/([^/]+)/);
      if (match && match[1]) {
        try {
          return decodeURIComponent(match[1]);
        } catch (error) {
          console.warn('[landing] decodeURIComponent failed, using raw value', error);
          return match[1];
        }
      }
      return null;
    }
  });
})();