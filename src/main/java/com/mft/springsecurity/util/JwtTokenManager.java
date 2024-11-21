package com.mft.springsecurity.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenManager {

    @Value("${jwt.secret}")
    private String sifreAnahtari ;

    public Optional<String> createToken(String username){
        String token;

        Long exDate = 1000L*60*60*24;// 1 gün
        try{
            /**
             * DİKKAT!!!  kullanıcı adı şifre Claim içine konulmaz.
             * Claim nesnesi olarak yerleştirdiğiniz bilgilerin açık okunur olduğunu asla unutmayınız.
             */
            token =  JWT.create()
                    .withAudience()
                    .withClaim("username",username) // Token içerisine eklemek istediğiniz nesneleri bununla ekliyoruz.
                    .withClaim("isOne",true)
                    .withIssuer("mft") // sahibi
                    .withIssuedAt(new Date()) // oluşturulma zamanı
                    .withExpiresAt(new Date(System.currentTimeMillis()+exDate)) // geçersiz olma zamanı
                    .sign(Algorithm.HMAC512(sifreAnahtari));
            return Optional.of(token);
        }catch (Exception ex){
            return Optional.empty();
        }
    }

    public Boolean validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC512(sifreAnahtari);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("mft")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            if(decodedJWT==null)
                return false;
        }catch (Exception exception){
            return false;
        }
        return true;
    }

    public Optional<String> getUsernameFromToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC512(sifreAnahtari);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("mft")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            if(decodedJWT==null)
                return Optional.empty();
            return Optional.of(decodedJWT.getClaim("username").asString());
        }catch (Exception exception){
            return Optional.empty();
        }

    }


}
