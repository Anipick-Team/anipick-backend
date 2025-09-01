// 토스 스타일 스크롤 애니메이션과 인터랙션
document.addEventListener('DOMContentLoaded', function() {
  initializeAnimations();
  initializeScrollEffects();
  initializeHeaderEffects();
  initializeParticles();
  initializeAdvancedEffects();
  initializeSmoothScroll();
  initializeMobileNavigation();
});

// 애니메이션 초기화
function initializeAnimations() {
  // Intersection Observer로 스크롤 애니메이션 설정
  const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  };

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('show');
        // 한 번만 실행되도록 unobserve
        observer.unobserve(entry.target);
      }
    });
  }, observerOptions);

  // 애니메이션 대상 요소들 관찰
  const animatedElements = document.querySelectorAll('.feature-section, .intro-section, .cta-section');
  animatedElements.forEach(el => {
    el.classList.add('fade-up');
    observer.observe(el);
  });

  // 히어로 섹션 애니메이션
  const heroContent = document.querySelector('.hero-content');
  const heroImage = document.querySelector('.hero-image');
  
  if (heroContent && heroImage) {
    setTimeout(() => {
      heroContent.style.animation = 'fadeInUp 1s ease forwards';
    }, 300);
    
    setTimeout(() => {
      heroImage.style.animation = 'fadeInUp 1s ease forwards';
    }, 600);
  }
}

// 스크롤 효과 초기화
function initializeScrollEffects() {
  let ticking = false;
  
  function updateScrollEffects() {
    const scrollY = window.scrollY;
    const windowHeight = window.innerHeight;
    
    // 패럴랙스 효과
    updateParallaxEffects(scrollY);
    
    // 스크롤 진행도에 따른 애니메이션
    updateScrollAnimations(scrollY, windowHeight);
    
    ticking = false;
  }
  
  function requestTick() {
    if (!ticking) {
      requestAnimationFrame(updateScrollEffects);
      ticking = true;
    }
  }
  
  window.addEventListener('scroll', requestTick, { passive: true });
}

// 패럴랙스 효과
function updateParallaxEffects(scrollY) {
  const heroBackground = document.querySelector('.hero-gradient');
  if (heroBackground) {
    const speed = scrollY * 0.5;
    heroBackground.style.transform = `translateY(${speed}px)`;
  }
}

// 스크롤 애니메이션
function updateScrollAnimations(scrollY, windowHeight) {
  const sections = document.querySelectorAll('.feature-section');
  
  sections.forEach((section, index) => {
    const rect = section.getBoundingClientRect();
    const sectionTop = rect.top;
    const sectionHeight = rect.height;
    
    // 섹션이 화면에 들어왔을 때
    if (sectionTop < windowHeight && sectionTop > -sectionHeight) {
      const progress = Math.max(0, Math.min(1, (windowHeight - sectionTop) / windowHeight));
      
      // 텍스트 애니메이션
      const featureText = section.querySelector('.feature-text');
      if (featureText) {
        const textElements = featureText.querySelectorAll('h1, h2');
        textElements.forEach((el, i) => {
          const delay = i * 0.1;
          if (progress > delay) {
            el.style.opacity = Math.min(1, (progress - delay) * 2);
            el.style.transform = `translateY(${Math.max(0, 20 * (1 - (progress - delay) * 2))}px)`;
          }
        });
      }
      
      // 이미지 애니메이션
      const featureImage = section.querySelector('.feature-image');
      if (featureImage) {
        const phoneMockups = featureImage.querySelectorAll('.phone-mockup');
        phoneMockups.forEach((mockup, i) => {
          const delay = 0.2 + i * 0.1;
          if (progress > delay) {
            mockup.style.opacity = Math.min(1, (progress - delay) * 2);
            mockup.style.transform = `translateY(${Math.max(0, 30 * (1 - (progress - delay) * 2))}px) scale(${0.9 + 0.1 * Math.min(1, (progress - delay) * 2)})`;
          }
        });
      }
      
      // 설명 텍스트 애니메이션
      const featureDescription = section.querySelector('.feature-description');
      if (featureDescription) {
        const delay = 0.4;
        if (progress > delay) {
          featureDescription.style.opacity = Math.min(1, (progress - delay) * 2);
          featureDescription.style.transform = `translateY(${Math.max(0, 20 * (1 - (progress - delay) * 2))}px)`;
        }
      }
    }
  });
}

// 헤더 효과 초기화
function initializeHeaderEffects() {
  const header = document.querySelector('.header');
  
  function updateHeader() {
    const currentScrollY = window.scrollY;
    
    // 헤더는 항상 보이도록 설정 (숨김 효과 제거)
    header.style.transform = 'translateY(0)';
    header.style.visibility = 'visible';
    header.style.opacity = '1';
    
    // 스크롤 위치에 따른 헤더 배경 투명도 조절
    if (currentScrollY > 50) {
      header.style.background = 'rgba(255, 255, 255, 0.98)';
      header.style.backdropFilter = 'blur(20px)';
      header.style.boxShadow = '0 4px 20px rgba(80, 200, 184, 0.1)';
    } else {
      header.style.background = 'rgba(255, 255, 255, 0.95)';
      header.style.backdropFilter = 'blur(10px)';
      header.style.boxShadow = '0 4px 20px rgba(80, 200, 184, 0.05)';
    }
  }
  
  window.addEventListener('scroll', updateHeader, { passive: true });
}

// 부드러운 스크롤 (앵커 링크)
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
  anchor.addEventListener('click', function (e) {
    e.preventDefault();
    const target = document.querySelector(this.getAttribute('href'));
    if (target) {
      const headerHeight = 60;
      const targetPosition = target.offsetTop - headerHeight;
      
      window.scrollTo({
        top: targetPosition,
        behavior: 'smooth'
      });
    }
  });
});

// 마우스 호버 효과
document.querySelectorAll('.store-btn, .store-link, .cta-button').forEach(button => {
  button.addEventListener('mouseenter', function() {
    this.style.transform = 'translateY(-2px) scale(1.02)';
  });
  
  button.addEventListener('mouseleave', function() {
    this.style.transform = 'translateY(0) scale(1)';
  });
});

// 터치 디바이스 지원
if ('ontouchstart' in window) {
  document.querySelectorAll('.phone-mockup').forEach(mockup => {
    mockup.addEventListener('touchstart', function() {
      this.style.transform = 'scale(0.98)';
    });
    
    mockup.addEventListener('touchend', function() {
      this.style.transform = 'scale(1)';
    });
  });
}

// 리사이즈 이벤트 처리
let resizeTimeout;
window.addEventListener('resize', function() {
  clearTimeout(resizeTimeout);
  resizeTimeout = setTimeout(function() {
    // 리사이즈 후 애니메이션 재계산
    updateScrollAnimations(window.scrollY, window.innerHeight);
  }, 250);
});

// 페이지 로드 완료 후 초기 애니메이션 실행
window.addEventListener('load', function() {
  // 히어로 섹션 초기 애니메이션
  const heroElements = document.querySelectorAll('.hero-content > *');
  heroElements.forEach((el, index) => {
    setTimeout(() => {
      el.style.opacity = '1';
      el.style.transform = 'translateY(0)';
    }, index * 200);
  });
});

// 성능 최적화를 위한 디바운스 함수
function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

// 스크롤 이벤트 최적화
const optimizedScrollHandler = debounce(function() {
  updateScrollAnimations(window.scrollY, window.innerHeight);
}, 16); // 60fps

window.addEventListener('scroll', optimizedScrollHandler, { passive: true });

// 파티클 시스템 초기화
function initializeParticles() {
  const particlesContainer = document.getElementById('particles');
  if (!particlesContainer) return;
  
  const particleCount = 50;
  
  for (let i = 0; i < particleCount; i++) {
    createParticle(particlesContainer);
  }
  
  // CTA 파티클도 초기화
  initializeCTAParticles();
}

// CTA 파티클 초기화
function initializeCTAParticles() {
  const ctaParticlesContainer = document.getElementById('cta-particles');
  if (!ctaParticlesContainer) return;
  
  const ctaParticleCount = 30;
  
  for (let i = 0; i < ctaParticleCount; i++) {
    createCTAParticle(ctaParticlesContainer);
  }
}

function createCTAParticle(container) {
  const particle = document.createElement('div');
  particle.className = 'cta-particle';
  
  // 랜덤 위치와 크기
  particle.style.left = Math.random() * 100 + '%';
  particle.style.width = particle.style.height = (Math.random() * 2 + 1) + 'px';
  
  // 랜덤 애니메이션 지연
  particle.style.animationDelay = Math.random() * 10 + 's';
  particle.style.animationDuration = (Math.random() * 10 + 10) + 's';
  
  // 흰색 계열의 랜덤 색상
  const colors = [
    'rgba(255, 255, 255, 0.6)',
    'rgba(255, 255, 255, 0.4)',
    'rgba(255, 255, 255, 0.3)',
    'rgba(92, 201, 255, 0.5)'
  ];
  particle.style.background = colors[Math.floor(Math.random() * colors.length)];
  
  container.appendChild(particle);
  
  // 애니메이션 완료 후 파티클 재생성
  particle.addEventListener('animationend', () => {
    particle.remove();
    setTimeout(() => createCTAParticle(container), Math.random() * 2000);
  });
}

function createParticle(container) {
  const particle = document.createElement('div');
  particle.className = 'particle';
  
  // 랜덤 위치와 크기
  particle.style.left = Math.random() * 100 + '%';
  particle.style.width = particle.style.height = (Math.random() * 3 + 1) + 'px';
  
  // 랜덤 애니메이션 지연
  particle.style.animationDelay = Math.random() * 10 + 's';
  particle.style.animationDuration = (Math.random() * 10 + 10) + 's';
  
  // 랜덤 색상
  const colors = [
    'rgba(92, 201, 255, 0.6)',
    'rgba(49, 130, 246, 0.6)',
    'rgba(138, 43, 226, 0.4)',
    'rgba(255, 255, 255, 0.3)'
  ];
  particle.style.background = colors[Math.floor(Math.random() * colors.length)];
  
  container.appendChild(particle);
  
  // 애니메이션 완료 후 파티클 재생성
  particle.addEventListener('animationend', () => {
    particle.remove();
    setTimeout(() => createParticle(container), Math.random() * 2000);
  });
}

// 고급 효과 초기화
function initializeAdvancedEffects() {
  // 마우스 추적 효과
  initializeMouseTracking();
  
  // 타이핑 효과
  initializeTypingEffect();
  
  // 카운터 애니메이션
  initializeCounterAnimation();
  
  // 글래스모피즘 효과
  initializeGlassmorphism();
}

// 마우스 추적 효과
function initializeMouseTracking() {
  const hero = document.querySelector('.hero');
  if (!hero) return;
  
  hero.addEventListener('mousemove', (e) => {
    const { clientX, clientY } = e;
    const { innerWidth, innerHeight } = window;
    
    const xPos = (clientX / innerWidth) * 100;
    const yPos = (clientY / innerHeight) * 100;
    
    // 그라데이션 위치 업데이트
    const gradient = document.querySelector('.hero-gradient');
    if (gradient) {
      gradient.style.background = `
        radial-gradient(circle at ${xPos}% ${yPos}%, rgba(92, 201, 255, 0.4) 0%, transparent 50%),
        radial-gradient(circle at ${100 - xPos}% ${100 - yPos}%, rgba(49, 130, 246, 0.3) 0%, transparent 50%),
        radial-gradient(circle at 50% 50%, rgba(138, 43, 226, 0.2) 0%, transparent 70%)
      `;
    }
    
    // 플로팅 셰이프 위치 업데이트
    const shapes = document.querySelectorAll('.shape');
    shapes.forEach((shape, index) => {
      const speed = (index + 1) * 0.02;
      const x = (xPos - 50) * speed;
      const y = (yPos - 50) * speed;
      shape.style.transform = `translate(${x}px, ${y}px)`;
    });
    
    // 폰 목업 3D 모션 효과
    const phoneMockup = document.querySelector('.hero .phone-mockup');
    if (phoneMockup) {
      const centerX = innerWidth / 2;
      const centerY = innerHeight / 2;
      
      // 마우스 위치에 따른 회전 각도 계산
      const rotateX = (clientY - centerY) / centerY * 10; // -10도 ~ +10도
      const rotateY = (clientX - centerX) / centerX * 10; // -10도 ~ +10도
      
      // 부드러운 3D 회전 적용
      phoneMockup.style.transform = `perspective(1000px) rotateX(${-rotateX}deg) rotateY(${rotateY}deg) scale(1.05)`;
    }
  });
  
  // 마우스가 hero 영역을 벗어날 때 원래 상태로 복원
  hero.addEventListener('mouseleave', () => {
    const phoneMockup = document.querySelector('.hero .phone-mockup');
    if (phoneMockup) {
      phoneMockup.style.transform = 'perspective(1000px) rotateX(0deg) rotateY(0deg) scale(1)';
    }
  });
  
  // 기능 섹션의 폰 목업들에도 3D 모션 효과 추가
  initializeFeatureSectionMouseTracking();
}

// 타이핑 효과
function initializeTypingEffect() {
  const heroTitle = document.querySelector('.hero-text h1');
  if (!heroTitle) return;
  
  const text = heroTitle.textContent;
  heroTitle.textContent = '';
  
  let i = 0;
  const typeWriter = () => {
    if (i < text.length) {
      heroTitle.textContent += text.charAt(i);
      i++;
      setTimeout(typeWriter, 50);
    }
  };
  
  // 페이지 로드 후 1초 뒤 시작
  setTimeout(typeWriter, 1000);
}

// 카운터 애니메이션
function initializeCounterAnimation() {
  const stats = document.querySelectorAll('.stat-number');
  const heroHighlights = document.querySelectorAll('.highlight-number');
  
  const animateCounter = (element, target) => {
    let current = 0;
    const increment = target / 100;
    const timer = setInterval(() => {
      current += increment;
      if (current >= target) {
        current = target;
        clearInterval(timer);
      }
      element.textContent = Math.floor(current) + (target.toString().includes('+') ? '+' : '');
    }, 20);
  };

  const animateCounterWithSuffix = (element, target, suffix) => {
    let current = 0;
    const increment = target / 100;
    const timer = setInterval(() => {
      current += increment;
      if (current >= target) {
        current = target;
        clearInterval(timer);
      }
      element.textContent = Math.floor(current) + suffix + '+';
    }, 20);
  };
  
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const target = entry.target.textContent;
        if (target.includes('18K')) {
          animateCounterWithSuffix(entry.target, 18, 'K');
        } else if (target.includes('30')) {
          animateCounter(entry.target, 30);
        } else if (target.includes('18개')) {
          // 18개는 텍스트이므로 카운터 애니메이션 제외
        }
        observer.unobserve(entry.target);
      }
    });
  });
  
  stats.forEach(stat => observer.observe(stat));
  
  // 히어로 하이라이트 카운터 애니메이션
  const heroObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const target = entry.target.textContent;
        if (target.includes('18K')) {
          animateCounterWithSuffix(entry.target, 18, 'K');
        } else if (target.includes('18개')) {
          // 18개는 텍스트이므로 카운터 애니메이션 제외
        } else if (target.includes('30')) {
          animateCounter(entry.target, 30);
        }
        heroObserver.unobserve(entry.target);
      }
    });
  });
  
  heroHighlights.forEach(highlight => heroObserver.observe(highlight));
}

// 기능 섹션 마우스 추적 효과
function initializeFeatureSectionMouseTracking() {
  const featureSections = document.querySelectorAll('.feature-section');
  
  featureSections.forEach(section => {
    const phoneMockups = section.querySelectorAll('.phone-mockup');
    
    section.addEventListener('mousemove', (e) => {
      const rect = section.getBoundingClientRect();
      const centerX = rect.left + rect.width / 2;
      const centerY = rect.top + rect.height / 2;
      
      phoneMockups.forEach((mockup, index) => {
        const { clientX, clientY } = e;
        
        // 마우스 위치에 따른 회전 각도 계산 (더 작은 각도로 설정)
        const rotateX = (clientY - centerY) / (rect.height / 2) * 8; // -8도 ~ +8도
        const rotateY = (clientX - centerX) / (rect.width / 2) * 8; // -8도 ~ +8도
        
        // 인덱스에 따른 약간의 차이를 주어 자연스러운 효과
        const scale = 1.02 + (index * 0.01);
        const delay = index * 0.05;
        
        // 부드러운 3D 회전 적용
        mockup.style.transform = `perspective(800px) rotateX(${-rotateX}deg) rotateY(${rotateY}deg) scale(${scale})`;
        mockup.style.transitionDelay = `${delay}s`;
      });
    });
    
    // 마우스가 섹션을 벗어날 때 원래 상태로 복원
    section.addEventListener('mouseleave', () => {
      phoneMockups.forEach((mockup, index) => {
        const delay = index * 0.05;
        mockup.style.transitionDelay = `${delay}s`;
        mockup.style.transform = 'perspective(800px) rotateX(0deg) rotateY(0deg) scale(1)';
      });
    });
  });
}

// 글래스모피즘 효과
function initializeGlassmorphism() {
  const glassElements = document.querySelectorAll('.store-btn, .stat, .feature-badge');
  
  glassElements.forEach(element => {
    element.addEventListener('mouseenter', () => {
      element.style.backdropFilter = 'blur(20px)';
      element.style.background = 'rgba(255, 255, 255, 0.2)';
    });
    
    element.addEventListener('mouseleave', () => {
      element.style.backdropFilter = 'blur(10px)';
      element.style.background = '';
    });
  });
}

// 고급 호버 효과
document.querySelectorAll('.feature-section').forEach(section => {
  section.addEventListener('mouseenter', () => {
    section.style.transform = 'translateY(-5px)';
    section.style.boxShadow = '0 20px 60px rgba(0, 0, 0, 0.1)';
  });
  
  section.addEventListener('mouseleave', () => {
    section.style.transform = 'translateY(0)';
    section.style.boxShadow = 'none';
  });
});

// 스크롤 진행도는 헤더의 .header-progress만 사용
// 별도로 생성하지 않음

// 부드러운 스크롤 네비게이션
function initializeSmoothScroll() {
  // 네비게이션 클릭 시 부드러운 스크롤
  document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      const targetId = link.getAttribute('data-section');
      const targetSection = document.getElementById(targetId);
      
      if (targetSection) {
        const headerHeight = 60;
        const targetPosition = targetSection.offsetTop - headerHeight;
        
        window.scrollTo({
          top: targetPosition,
          behavior: 'smooth'
        });
      }
    });
  });
  
  // 스크롤 위치에 따른 네비게이션 활성화
  window.addEventListener('scroll', () => {
    const sections = ['home', 'intro', 'recommend', 'ranking', 'explore', 'upcoming', 'download'];
    const scrollY = window.scrollY;
    const windowHeight = window.innerHeight;
    
    sections.forEach((sectionId, index) => {
      const section = document.getElementById(sectionId);
      if (section) {
        const sectionTop = section.offsetTop - 100;
        const sectionBottom = sectionTop + section.offsetHeight;
        
        if (scrollY >= sectionTop && scrollY < sectionBottom) {
          updateActiveNavigation(index);
        }
      }
    });
  });
}

// 활성 네비게이션 업데이트
function updateActiveNavigation(activeIndex) {
  const navLinks = document.querySelectorAll('.nav-link');
  
  navLinks.forEach((link, index) => {
    if (index === activeIndex - 1) { // nav-link는 home 제외
      link.classList.add('active');
    } else {
      link.classList.remove('active');
    }
  });
  
  // 헤더 진행도 업데이트
  const progressBar = document.querySelector('.header-progress');
  if (progressBar) {
    const sections = ['home', 'intro', 'recommend', 'ranking', 'explore', 'upcoming', 'download'];
    const progress = (activeIndex / (sections.length - 1)) * 100;
    progressBar.style.width = progress + '%';
  }
  
  // 전체 페이지 스크롤 진행도도 업데이트
  const scrollTop = window.pageYOffset;
  const docHeight = document.body.scrollHeight - window.innerHeight;
  const scrollPercent = (scrollTop / docHeight) * 100;
  if (progressBar) {
    progressBar.style.width = scrollPercent + '%';
  }
}

// 모바일 네비게이션 초기화
function initializeMobileNavigation() {
  const mobileToggle = document.querySelector('.mobile-menu-toggle');
  const navbar = document.querySelector('.navbar');
  
  if (!mobileToggle || !navbar) return;
  
  // 모바일 메뉴 토글
  mobileToggle.addEventListener('click', () => {
    mobileToggle.classList.toggle('active');
    navbar.classList.toggle('active');
    
    // 모바일 메뉴 표시/숨김
    if (navbar.classList.contains('active')) {
      showMobileMenu();
    } else {
      hideMobileMenu();
    }
  });
  
  // 모바일 메뉴 링크 클릭 시 모바일 메뉴만 닫기 (헤더는 유지)
  document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', () => {
      // 모바일에서만 메뉴 닫기
      if (window.innerWidth <= 768) {
        hideMobileMenu();
        mobileToggle.classList.remove('active');
      }
    });
  });
  
  // 화면 크기 변경 시 모바일 메뉴 숨김
  window.addEventListener('resize', () => {
    if (window.innerWidth > 768) {
      hideMobileMenu();
      mobileToggle.classList.remove('active');
    }
  });
}

function showMobileMenu() {
  const navbar = document.querySelector('.navbar');
  if (!navbar) return;
  
  navbar.style.display = 'block';
  navbar.style.position = 'fixed';
  navbar.style.top = '60px';
  navbar.style.left = '0';
  navbar.style.right = '0';
  navbar.style.background = 'rgba(255, 255, 255, 0.98)';
  navbar.style.backdropFilter = 'blur(20px)';
  navbar.style.padding = '20px';
  navbar.style.boxShadow = '0 4px 20px rgba(0, 0, 0, 0.1)';
  navbar.style.animation = 'slideDown 0.3s ease';
  
  const menu = navbar.querySelector('.navbar-menu');
  if (menu) {
    menu.style.flexDirection = 'column';
    menu.style.gap = '16px';
    menu.style.alignItems = 'stretch';
  }
  
  const navLinks = navbar.querySelectorAll('.nav-link');
  navLinks.forEach(link => {
    link.style.width = '100%';
    link.style.justifyContent = 'center';
    link.style.padding = '16px';
  });
}

function hideMobileMenu() {
  const navbar = document.querySelector('.navbar');
  if (!navbar) return;
  
  navbar.style.display = 'none';
  navbar.classList.remove('active');
}

// 터치 디바이스 지원 (기본 터치 이벤트만)
if ('ontouchstart' in window) {
  document.querySelectorAll('.phone-mockup').forEach(mockup => {
    mockup.addEventListener('touchstart', function() {
      this.style.transform = 'scale(0.98)';
    });
    
    mockup.addEventListener('touchend', function() {
      this.style.transform = 'scale(1)';
    });
  });
}

// 모바일 메뉴 애니메이션 CSS 추가
const style = document.createElement('style');
style.textContent = `
  @keyframes slideDown {
    from {
      opacity: 0;
      transform: translateY(-20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  
  .navbar.active {
    display: block !important;
  }
`;
document.head.appendChild(style);