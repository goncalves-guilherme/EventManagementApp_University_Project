using SilentManagerApiServer.BusinessLogic.Dtos;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Validations
{
    public class UserValidations
    {
        public IEnumerable<ErrorResult> ValidateUserCreation(UserInDto user)
        {
            IEnumerable<ErrorResult> errors = new List<ErrorResult>();
            errors = errors.Union(ValidateUserEmail(user.Email));
            errors.Union(ValidateUsername(user.Name));
            errors.Union(ValidatePassword(user.Password));

            return errors;
        }

        public IEnumerable<ErrorResult> ValidateUserEmail(string email)
        {
            List<ErrorResult> errors = new List<ErrorResult>();
            if (Regex.Match(email, @"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$").Success)
            {
                errors.Add(new ErrorResult(2, "Please enter a valid email"));
            }
            return errors;
        }

        public IEnumerable<ErrorResult> ValidateUsername(string username)
        {
            List<ErrorResult> errors = new List<ErrorResult>();
            if (Regex.Match(username, @"([<>\(){ }= ""'\s#$*])").Success)
            {
                errors.Add(new ErrorResult(2, "Sorry we only allow names from [a-z] (0-9) and periods with ."));
            }
            return errors;
        }

        public IEnumerable<ErrorResult> ValidatePassword(string password)
        {
            List<ErrorResult> errors = new List<ErrorResult>();
            if (password.Length>=6)
            {
                errors.Add(new ErrorResult(2, "Sorry we only allow password bigger than 5 characters"));
            }
            return errors;
        }

        public IEnumerable<ErrorResult> ValidateGetUserById(int id)
        {
            List<ErrorResult> errors = new List<ErrorResult>();
            if (id == 0)
            {
                errors.Add(new ErrorResult(1, "The user id cannot be 0 or higher than " + Int64.MaxValue));
                return errors;
            }

            return errors;
        }

    }
}
