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

    const DEEP_LINK_SCHEME = 'anipick://app/anime/detail/';
    const animeId = getAnimeIdFromPath();
    const deepLinkUrl = animeId ? `${DEEP_LINK_SCHEME}${animeId}` : '';

    const config = {
      deepLink: deepLinkUrl,
      animeId,
      storeUrls: {
        ios: (body.dataset.iosStore || '').trim(),
        android: (body.dataset.androidStore || '').trim()
      },
      attemptDelay: 400,
      reminderDelay: 2200,
      iframeCleanupDelay: 1600
    };

    const preferredPlatform = getPreferredPlatformFromQuery();
    const platform = detectPlatform(preferredPlatform);
    const storeButtons = document.querySelectorAll('.store-button');
    const openAppButton = document.querySelector('[data-open-app]');

    let autoDeepLinkAttempted = false;

    if (!config.deepLink) {
      handleMissingAnimeId();
      return;
    }

    updateHelperCopy(platform);
    decorateStoreButtons(storeButtons, config.storeUrls, platform);
    bindStoreButtons(storeButtons, config.storeUrls);

    if (openAppButton) {
      openAppButton.addEventListener('click', (event) => {
        event.preventDefault();
        attemptDeepLink('manual');
      });
    }

    window.addEventListener('load', () => {
      setTimeout(() => attemptDeepLink('auto'), config.attemptDelay);
    }, { once: true });

    function attemptDeepLink(trigger = 'manual') {
      if (!config.deepLink) {
        return;
      }
      if (trigger === 'auto' && autoDeepLinkAttempted) {
        return;
      }
      if (trigger === 'auto') {
        autoDeepLinkAttempted = true;
      }

      announceStatus('앱을 여는 중입니다...');

      if (platform === 'android') {
        const iframe = document.createElement('iframe');
        iframe.style.display = 'none';
        iframe.src = config.deepLink;
        document.body.appendChild(iframe);
        setTimeout(() => {
          iframe.remove();
        }, config.iframeCleanupDelay);
      } else if (platform === 'ios') {
        window.location.href = config.deepLink;
      } else {
        // 데스크톱 등 기타 환경
        window.location.href = config.deepLink;
      }

      if (config.reminderDelay) {
        setTimeout(() => updateHelperCopy(platform), config.reminderDelay);
      }
    }

    function updateHelperCopy(currentPlatform) {
      const fallbackText = currentPlatform === 'ios'
        ? '앱이 열리지 않는다면 아래 애플 앱스토어 버튼을 눌러 설치해 주세요.'
        : currentPlatform === 'android'
          ? '앱이 열리지 않는다면 아래 구글 플레이 스토어 버튼을 눌러 설치해 주세요.'
          : '앱이 열리지 않는다면 사용 중인 기기에 맞는 스토어 버튼을 눌러 설치해 주세요.';

      announceStatus(`랜딩 페이지에 접속하면 자동으로 앱 열기를 시도합니다.<br>${fallbackText}`);
    }

    function announceStatus(message) {
      if (!statusElement) return;
      statusElement.innerHTML = message;
    }

    function handleMissingAnimeId() {
      announceStatus('애니 정보를 확인할 수 없어 앱 열기를 건너뜁니다.');
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
        if (!target || !urls[target]) {
          return;
        }

        button.addEventListener('click', () => {
          window.location.href = urls[target];
        });
      });
    }

    function getPreferredPlatformFromQuery() {
      const params = new URLSearchParams(window.location.search);
      const keys = ['platform', 'store', 'os'];
      for (const key of keys) {
        const value = params.get(key);
        const normalized = normalizePlatform(value);
        if (normalized) {
          return normalized;
        }
      }
      return null;
    }

    function detectPlatform(explicitPlatform) {
      if (explicitPlatform) {
        return explicitPlatform;
      }

      const ua = navigator.userAgent || navigator.vendor || window.opera || '';
      if (/android/i.test(ua)) {
        return 'android';
      }

      const isIOS = /iPad|iPhone|iPod/.test(ua) ||
        (ua.includes('Mac') && 'ontouchend' in document);

      if (isIOS) {
        return 'ios';
      }

      return 'unknown';
    }

    function normalizePlatform(value) {
      if (!value) return null;
      const lowered = value.toString().toLowerCase();
      if (lowered === 'ios' || lowered === 'apple') {
        return 'ios';
      }
      if (lowered === 'android') {
        return 'android';
      }
      return null;
    }

    function getAnimeIdFromPath() {
      const match = window.location.pathname.match(/\/app\/anime\/detail\/([^/]+)/);
      if (match && match[1]) {
        try {
          return decodeURIComponent(match[1]);
        } catch (error) {
          return match[1];
        }
      }
      return null;
    }
  });
})();
