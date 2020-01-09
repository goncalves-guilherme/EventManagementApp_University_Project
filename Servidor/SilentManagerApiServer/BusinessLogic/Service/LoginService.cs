using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Principal;
using Microsoft.AspNetCore.Identity;
using Microsoft.IdentityModel.Tokens;
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.DataAccess;
using SilentManager.API.BusinessLogic.Service;
using System.Collections.Generic;
using System.Text;
using System.Linq;
using System.Threading.Tasks;
using AutoMapper;
using System.Security.Cryptography;
using SilentManagerApiServer.Models.DB;
using SilentManager.API.Security;
using SilentManager.API.BusinessLogic.Validations;

//https://github.com/renatogroffe/ASPNETCore2.2_JWT-Identity/blob/master/APIProdutos/Security/AccessManager.cs
namespace SilentManagerApiServer.BusinessLogic.Service
{
    public class LoginService : ILoginService
    {

        private readonly IUnitOfWork _unitOfWork;
        private readonly UserValidations userValid;


        public LoginService(IUnitOfWork _unitOfWork)
        {
            this._unitOfWork = _unitOfWork;
            this.userValid = new UserValidations();
        }

        public bool ValidateCredentials(SilentManager.API.Security.User user)
        {
            bool credenciaisValidas = false;
            if (user != null && !String.IsNullOrWhiteSpace(user.Email))
            {
                // Verifica a existência do usuário nas tabelas do
                // ASP.NET Core Identity
                SilentManager.API.Security.User userIdentity = _unitOfWork.UserRepo.Find(_user => _user.Email.Equals(user.Email)).Select(u=> new SilentManager.API.Security.User {
                    Email = u.Email,
                    Password = u.Password
                }).FirstOrDefault();
                if (userIdentity != null)
                {
                    user.Password = ComputeSha256Hash(user.Password);
                    // Efetua o login com base no Id do usuário e sua senha
                    credenciaisValidas = user.Password.Equals(userIdentity.Password) ? true : false;
                }
            }

            return credenciaisValidas;
        }

        public TokenDto GenerateToken(SilentManager.API.Security.User user)
        {

            DateTime dataCriacao = DateTime.Now;
            DateTime dataExpiracao = dataCriacao +
                TimeSpan.FromSeconds(3600);

            string _token = RandomString(32);

            TokenDto token = new TokenDto
            {
                Expiration = dataExpiracao,
                AccessToken = _token
            };

            Token tk = new Token
            {
                UserId = _unitOfWork.UserRepo.Find(u => u.Email.Equals(user.Email)).FirstOrDefault().UserId,
                Expiration_Date = dataExpiracao,
                token = _token
            };

            _unitOfWork.tokenRepo.Add(tk);
            _unitOfWork.tokenRepo.Save();

            return token;

        }

        public string RandomString(int length)
        {
            string allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            if (length < 0) throw new ArgumentOutOfRangeException("length", "length cannot be less than zero.");
            if (string.IsNullOrEmpty(allowedChars)) throw new ArgumentException("allowedChars may not be empty.");

            const int byteSize = 0x100;
            var allowedCharSet = new HashSet<char>(allowedChars).ToArray();
            if (byteSize < allowedCharSet.Length) throw new ArgumentException(String.Format("allowedChars may contain no more than {0} characters.", byteSize));

            // Guid.NewGuid and System.Random are not particularly random. By using a
            // cryptographically-secure random number generator, the caller is always
            // protected, regardless of use.
            using (var rng = RandomNumberGenerator.Create())
            {
                var result = new StringBuilder();
                var buf = new byte[128];
                while (result.Length < length)
                {
                    rng.GetBytes(buf);
                    for (var i = 0; i < buf.Length && result.Length < length; ++i)
                    {
                        // Divide the byte into allowedCharSet-sized groups. If the
                        // random value falls into the last group and the last group is
                        // too small to choose from the entire allowedCharSet, ignore
                        // the value in order to avoid biasing the result.
                        var outOfRangeStart = byteSize - (byteSize % allowedCharSet.Length);
                        if (outOfRangeStart <= buf[i]) continue;
                        result.Append(allowedCharSet[buf[i] % allowedCharSet.Length]);
                    }
                }
                return result.ToString();
            }
        }

        public async Task<ResultWrapper<UserOutDto>> CreateUser(UserInDto user)
        {
            var errors = userValid.ValidateUserCreation(user);
            if (errors.Count() > 0)
            {
                return new ResultWrapper<UserOutDto>(null, errors);
            }

            user.Password = ComputeSha256Hash(user.Password);
            var _user = Mapper.Map<Models.DB.User>(user);
            _unitOfWork.UserRepo.AddAsync(_user);
            await _unitOfWork.CompleteAsync();

            return new ResultWrapper<UserOutDto>(Mapper.Map<UserOutDto>(_user), null);
        }

        private string ComputeSha256Hash(string rawData)
        {
            // Create a SHA256   
            using (SHA256 sha256Hash = SHA256.Create())
            {
                // ComputeHash - returns byte array  
                byte[] bytes = sha256Hash.ComputeHash(Encoding.UTF8.GetBytes(rawData));

                // Convert byte array to a string   
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < bytes.Length; i++)
                {
                    builder.Append(bytes[i].ToString("x2"));
                }
                return builder.ToString();
            }


        }
    }
}