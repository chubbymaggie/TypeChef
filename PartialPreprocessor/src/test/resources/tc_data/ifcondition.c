#ifdef X
    #define VAL 3
#else
    #define VAL 0
#endif



#if (defined(X) ? 3 : 0) == 3

  _X is defined  CC1

#endif

#if VAL == 3

  _X is defined CC2

#endif

#if ((defined(X) ? 3 : 0) == 3) && !(VAL == 3)
  dead code
#endif



#if defined (__GNU_LIBRARY__) ? defined (__USE_GNU) : !defined (__STRICT_ANSI__)
  other condition CC3
#endif